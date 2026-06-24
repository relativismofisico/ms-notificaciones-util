# ms-notificaciones-util

Microservicio de notificaciones para la plataforma **Factoring**. Se encarga del envío de correos electrónicos vía SMTP y de la transferencia de archivos a servidores remotos mediante SFTP, actuando como canal de comunicación centralizado entre los distintos microservicios del ecosistema.

---

## Tabla de contenido

- [¿Qué hace este microservicio?](#qué-hace-este-microservicio)
- [Arquitectura](#arquitectura)
- [Tecnologías y dependencias](#tecnologías-y-dependencias)
- [Comunicación con otros microservicios](#comunicación-con-otros-microservicios)
- [Tópicos Kafka](#tópicos-kafka)
- [API REST](#api-rest)
- [Seguridad](#seguridad)
- [Bases de datos](#bases-de-datos)
- [Patrones implementados](#patrones-implementados)
- [Calidad de código](#calidad-de-código)
- [Tests](#tests)
- [Configuración y variables de entorno](#configuración-y-variables-de-entorno)
- [Ejecución local](#ejecución-local)
- [Docker](#docker)
- [Healthcheck](#healthcheck)
- [Documentación API (Swagger)](#documentación-api-swagger)
- [Changelog](#changelog)

---

## ¿Qué hace este microservicio?

`ms-notificaciones-util` centraliza el envío de notificaciones de la plataforma Factoring a través de tres mecanismos:

| Mecanismo | Descripción |
|-----------|-------------|
| **REST HTTP** | Expone `POST /notificarCorreo` para que otros servicios envíen correos SMTP de forma directa y síncrona. |
| **Kafka (consumidor)** | Escucha tres tópicos para procesar eventos de forma asíncrona: instrucciones de pago, notificaciones generales de email y OTPs. |
| **Patrón Outbox** | Garantiza la entrega at-least-once de emails de OTP: persiste el evento en MongoDB antes de enviarlo y reintenta mediante scheduler cada 5 segundos. |

### Capacidades principales

- Envío de correos electrónicos con soporte para: destinatarios, CC, BCC, cuerpo en texto plano o HTML, firma y archivos adjuntos en Base64.
- Transferencia de archivos a servidores SFTP (instrucciones de pago con canal `FTP`).
- Renderizado de plantillas de email almacenadas en base de datos (Mustache + Thymeleaf).
- Registro de cada envío en MongoDB (`email_log`).
- Resolución dinámica del destinatario según el tipo de actor: usuario o empresa.

---

## Arquitectura

El microservicio aplica **arquitectura hexagonal** validada con ArchUnit (13 reglas). Las capas son:

```
┌──────────────────────────────────────────────────────────────┐
│                    INFRAESTRUCTURA                            │
│  ┌────────────┐  ┌─────────────┐  ┌──────────┐  ┌────────┐  │
│  │ Controller │  │Kafka Consumer│  │ Scheduler│  │Security│  │
│  └─────┬──────┘  └──────┬──────┘  └────┬─────┘  └────────┘  │
│        │                │              │                      │
├────────▼────────────────▼──────────────▼─────────────────────┤
│                  APLICACIÓN (Services)                        │
│  NotificacionesCorreoService · EmailProcessorService          │
│  OtpEmailService · FtpService · OutboxService                 │
│  TemplateService · EmailSenderService · CanalProcessorFactory │
├──────────────────────────────────────────────────────────────┤
│                      DOMINIO                                  │
│  Entities · Events · Enums                                    │
├──────────────────────────────────────────────────────────────┤
│                 PUERTOS DE SALIDA                             │
│  Repositories (JPA + MongoDB)  ·  Client (SMTP)              │
└──────────────────────────────────────────────────────────────┘
```

**Reglas de arquitectura verificadas:**
- El dominio no depende de ninguna capa superior.
- Los servicios no dependen de adaptadores de infraestructura.
- Los controladores solo acceden a interfaces de servicio (nunca a `*Impl`).
- Los consumers Kafka no dependen de controladores ni schedulers.
- No existen dependencias cíclicas entre paquetes.
- Las clases `@Service` residen en el paquete `service`.
- Las clases `@RestController` residen en el paquete `controller`.
- Toda clase `*Impl` implementa al menos una interfaz.

---

## Tecnologías y dependencias

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | Java | 17 |
| Framework | Spring Boot | 3.5.4 |
| Build | Gradle | - |
| Mensajería | Apache Kafka | Spring Cloud 2025.0.0 |
| Email | Spring Boot Mail (SMTP) | - |
| Transferencia | Spring Integration SFTP / JSch | 0.1.55 |
| Base de datos relacional | MySQL | `mysql-connector-j` |
| Base de datos documental | MongoDB | Spring Data MongoDB |
| Templates | Mustache (compiler 0.9.10) + Thymeleaf | - |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) | - |
| Documentación API | SpringDoc OpenAPI 3 | 2.8.3 |
| Validación | Hibernate Validator | - |
| Observabilidad | Spring Boot Actuator | - |
| Utilidades | Lombok, Gson, Apache Commons | - |
| Tests unitarios | Spock Framework 2.4 (Groovy 4.0) | - |
| Tests de arquitectura | ArchUnit | - |
| Tests de rendimiento | Apache JMeter | - |
| Calidad estática | Checkstyle, PMD, SpotBugs, JaCoCo | - |
| Contenedor | Docker (multi-stage, Eclipse Temurin 17 Alpine) | - |

---

## Comunicación con otros microservicios

```
                         ┌──────────────────────────┐
  ms-negociador-orq ────►│                          │
  ms-carga-facturas ────►│  Apache Kafka (topics)   │
  ms-security       ────►│                          │
  ms-instrucciones  ────►└──────────┬───────────────┘
                                    │ consume
                         ┌──────────▼───────────────┐
    REST (JWT) ─────────►│  ms-notificaciones-util  │
                         └──────┬──────────┬─────────┘
                                │          │
                    ┌───────────▼──┐   ┌───▼────────────┐
                    │ SMTP Gmail   │   │ Servidor SFTP  │
                    │ (puerto 587) │   │ por empresa    │
                    └──────────────┘   └────────────────┘
                         │
               ┌─────────▼──────────┐
               │  MySQL (JPA)       │  ← plantillas, usuarios, FTP config
               │  MongoDB           │  ← outbox_event, email_log
               └────────────────────┘
```

### Microservicios productores (Kafka)

| Microservicio | Tópico que produce | Evento |
|---------------|--------------------|--------|
| `ms-negociador-orq` | `notificacion.email` | `NotificacionEmailEvent` |
| `ms-carga-facturas` | `notificacion.email` | `NotificacionEmailEvent` |
| `ms-instrucciones-util` | `instrucciones.generadas` | `InstruccionPagoEvent` |
| `ms-security` | `otp.created` | `OtpCreatedEvent` |

### Autenticación

Los tokens JWT son emitidos por **`ms-security`**. Este microservicio valida la firma y extrae el claim `rol` para la autorización.

---

## Tópicos Kafka

| Tópico | Consumer group | Evento consumido | Acción |
|--------|---------------|-----------------|--------|
| `notificacion.email` | `notificador-group` | `NotificacionEmailEvent` | Resuelve actor (usuario o empresa), renderiza plantilla y envía email. |
| `instrucciones.generadas` | `notificaciones-group` | `InstruccionPagoEvent` | Selecciona canal (`EMAIL` o `FTP`) y procesa: envía email con detalle de instrucción o sube archivo al SFTP de la empresa. |
| `otp.created` | `notificationsGroup` | `OtpCreatedEvent` | Persiste en Outbox (MongoDB) y el scheduler lo procesa cada 5 s para enviar el OTP por email. |
| `otp.email.sent` | _(productor)_ | - | Publicado por el microservicio al confirmar envío exitoso del OTP. |

---

## API REST

**Base path:** `/securityapi`  
**Puerto por defecto:** `8055`

### Endpoints

| Método | Ruta | Auth | Descripción | Response |
|--------|------|------|-------------|----------|
| `POST` | `/notificarCorreo` | JWT Bearer | Envía un correo electrónico vía SMTP. | `204 No Content` |
| `GET` | `/actuator/health` | Público | Estado del servicio (liveness/readiness). | `200 OK` |
| `GET` | `/swagger-ui.html` | Público | Documentación OpenAPI interactiva. | - |
| `GET` | `/v3/api-docs` | Público | Especificación OpenAPI 3 en JSON. | - |

### Cuerpo `POST /notificarCorreo`

```json
{
  "nombreRemitente": "Plataforma Factoring",
  "correoRemitente": "notificaciones@factoring.co.com",
  "asunto": "Instrucción de pago #INS-2024-001",
  "destinatarios": ["empresa@cliente.com"],
  "destinatariosCc": ["supervisor@factoring.co.com"],
  "destinatariosBcc": ["auditoria@factoring.co.com"],
  "cuerpoTexto": "Su instrucción ha sido procesada.",
  "cuerpoHtml": "<h1>Instrucción procesada</h1>",
  "firma": "Equipo Factoring | www.factoring.co.com",
  "adjuntos": [
    {
      "nombre": "instruccion.pdf",
      "contenidoBase64": "JVBERi0x...",
      "tipoMime": "application/pdf"
    }
  ]
}
```

### Códigos de respuesta HTTP

| Código | Descripción |
|--------|-------------|
| `204` | Correo enviado correctamente. Sin cuerpo de respuesta. |
| `400` | Solicitud inválida: campos requeridos ausentes o mal formados. |
| `401` | No autenticado: token JWT ausente, expirado o inválido. |
| `403` | Sin permisos: el rol del token no tiene acceso al endpoint. |
| `422` | Regla de negocio incumplida: email malformado o sin destinatarios. |
| `500` | Error interno del servidor. |

---

## Seguridad

- **Mecanismo:** JWT Bearer Token (stateless, sin sesión HTTP).
- **Emisor del token:** `ms-security`.
- **Claim leído:** `rol`.
- **Roles autorizados para `/notificarCorreo`:** `ADMINISTRADOR`, `EMPRESA`, `OPERARIO`, `FONDEADOR`.
- **Endpoints públicos:** `/v3/api-docs/**`, `/swagger-ui/**`, `/actuator/health/**`.
- **Variable de entorno:** `JWT_SECRET` (requerida en producción).

El filtro `JwtAuthFilter` intercepta cada request, valida la firma HMAC del token y carga el contexto de seguridad de Spring antes de que llegue al controlador.

---

## Bases de datos

### MySQL (Spring Data JPA)

Almacena los datos maestros del negocio:

| Tabla | Entidad | Descripción |
|-------|---------|-------------|
| `messages_templates` | `MessageTemplate` | Plantillas de email con contenido HTML/texto y asunto. |
| `ftp_configuracion` | `ConfiguracionFtp` | Credenciales y parámetros SFTP por empresa (host, puerto, usuario, clave privada, fingerprint). |
| `person` / `user` | `Person`, `User` | Personas y usuarios de la plataforma para resolver destinatarios. |
| `company` | `Company` | Empresas para resolver destinatarios corporativos. |

### MongoDB

Almacena documentos de trazabilidad y entrega garantizada:

| Colección | Entidad | Descripción |
|-----------|---------|-------------|
| `outbox_event` | `OutboxEventEntity` | Eventos pendientes de procesamiento (patrón Outbox). Campos: `msPropietario`, `enviado`, `intentos`, `fechaCreacion`. |
| `email_log` | `EmailLog` | Registro de cada envío: destinatario, asunto, estado, error y fecha. |

---

## Patrones implementados

| Patrón | Dónde | Propósito |
|--------|-------|-----------|
| **Strategy** | `CanalProcessorFactory` + `CanalProcessor` | Selecciona el procesador correcto (`EmailCanalProcessor` o `FtpCanalProcessor`) según el `CanalEnvio` del evento. |
| **Strategy** | `ActorEmailResolverFactory` + `ActorEmailResolver` | Resuelve el email del destinatario según tipo de actor (usuario o empresa). |
| **Factory** | `CanalProcessorFactory`, `ActorEmailResolverFactory` | Encapsula la creación del objeto de estrategia correspondiente. |
| **Outbox** | `OutboxService`, `OtpEmailScheduler` | Garantiza entrega at-least-once de OTPs: guarda en MongoDB antes de publicar a Kafka, scheduler reintenta cada 5 s. |
| **Template Method** | `TemplateService`, `TemplateRenderer` | Renderiza plantillas Mustache/Thymeleaf con variables dinámicas desde base de datos. |

---

## Calidad de código

| Herramienta | Umbral | Estado |
|-------------|--------|--------|
| **JaCoCo** | Cobertura de líneas >= 99% | Configurado |
| **Checkstyle** | 0 violaciones | Configurado |
| **PMD** | 0 violaciones (ruleset personalizado) | Configurado |
| **SpotBugs** | 0 bugs (excludes configurados) | Configurado |
| **ArchUnit** | 13 reglas hexagonales sin violaciones | Verificado en CI |

---

## Tests

### Tests unitarios (Spock)

Ubicados en `src/test/groovy`. Cubren todos los servicios, utilidades, seguridad y validaciones.

```bash
./gradlew test
```

El reporte HTML de JaCoCo se genera en `build/reports/jacoco/test/html/index.html`.

### Tests de arquitectura (ArchUnit)

Ubicados en `src/architecture-test/java`. Se ejecutan validando las 13 reglas hexagonales.

```bash
./gradlew archTest
```

### Tests de rendimiento (JMeter)

Ubicados en `external-test/jmeter/plans/`. Tres perfiles de carga disponibles:

| Plan | Archivo | Descripción |
|------|---------|-------------|
| Smoke | `ms-notificaciones-smoke.jmx` | Validación básica de disponibilidad. |
| Carga | `ms-notificaciones-carga.jmx` | Simulación de carga sostenida. |
| Concurrencia | `ms-notificaciones-concurrencia.jmx` | Prueba de usuarios concurrentes. |

Los reportes HTML se almacenan en `external-test/jmeter/reports/`.

---

## Configuración y variables de entorno

El microservicio usa perfiles de Spring Boot:

| Perfil | Archivo | Uso |
|--------|---------|-----|
| _(default)_ | `application.yml` | Configuración base y local. |
| `dev` | `application-dev.yml` | Entorno de desarrollo. |
| `qa` | `application-qa.yml` | Entorno de QA. |
| `prod` | `application-prod.yml` | Producción. |

### Variables de entorno requeridas en producción

| Variable | Descripción | Valor por defecto (solo local) |
|----------|-------------|-------------------------------|
| `JWT_SECRET` | Clave HMAC para validar tokens JWT. | Valor hardcoded (no usar en prod) |
| `SPRING_DATASOURCE_URL` | URL JDBC de MySQL. | RDS de desarrollo |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos. | `desarrollador` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de base de datos. | - |
| `SPRING_DATA_MONGODB_URI` | URI de conexión a MongoDB. | `mongodb://localhost:27017/DB_Facturas` |
| `SPRING_KAFKA_CONSUMER_BOOTSTRAP_SERVERS` | Brokers Kafka. | `localhost:9092` |
| `SPRING_MAIL_USERNAME` | Cuenta SMTP remitente. | Cuenta Gmail de desarrollo |
| `SPRING_MAIL_PASSWORD` | Contraseña de aplicación SMTP. | App password Gmail |
| `SPRING_PROFILES_ACTIVE` | Perfil activo. | _(ninguno)_ |

---

## Ejecución local

### Prerrequisitos

- Java 17
- Docker (para levantar MySQL, MongoDB y Kafka localmente)
- Gradle (o usar el wrapper incluido `./gradlew`)

### Pasos

```bash
# 1. Clonar el repositorio
git clone <url-repo>
cd ms-notificaciones-util

# 2. Levantar infraestructura local (MySQL, MongoDB, Kafka)
docker-compose up -d   # si existe docker-compose.yml en el ecosistema

# 3. Compilar y ejecutar
./gradlew bootRun

# 4. Verificar que el servicio está levantado
curl http://localhost:8055/securityapi/actuator/health
```

El servicio queda disponible en: `http://localhost:8055/securityapi`

---

## Docker

El `Dockerfile` usa construcción multi-stage para minimizar la imagen final:

- **Stage 1 (builder):** Eclipse Temurin 17 JDK Alpine — compila y genera el JAR.
- **Stage 2 (runtime):** Eclipse Temurin 17 JRE Alpine — ejecuta el JAR con usuario no root (`appuser`).

```bash
# Construir imagen
docker build -t ms-notificaciones-util:1.0.0 .

# Ejecutar contenedor
docker run -p 8055:8055 \
  -e JWT_SECRET=<secreto> \
  -e SPRING_PROFILES_ACTIVE=dev \
  ms-notificaciones-util:1.0.0
```

---

## Healthcheck

Spring Boot Actuator expone los siguientes endpoints de salud:

| Endpoint | Descripción |
|----------|-------------|
| `GET /securityapi/actuator/health` | Estado general del servicio. |
| `GET /securityapi/actuator/health/liveness` | Liveness probe (Kubernetes). |
| `GET /securityapi/actuator/health/readiness` | Readiness probe (Kubernetes). |

---

## Documentación API (Swagger)

Disponible sin autenticación en entornos locales y de desarrollo:

| URL | Descripción |
|-----|-------------|
| `http://localhost:8055/securityapi/swagger-ui.html` | Interfaz Swagger UI interactiva. |
| `http://localhost:8055/securityapi/v3/api-docs` | Especificación OpenAPI 3 en JSON. |
| `http://dev.factoring.co.com/ms-notificacionescorreo-util/swagger-ui.html` | Swagger en desarrollo. |

---

## Changelog

### [1.0.0] - 2026-06-24

#### Added
- **Commit inicial:** estructura base del microservicio Spring Boot con Gradle.
- **Envío de email SMTP:** integración con Gmail (STARTTLS, puerto 587), soporte para CC, BCC, cuerpo HTML/texto y adjuntos Base64.
- **Kafka consumers:** tres consumidores para los tópicos `notificacion.email`, `instrucciones.generadas` y `otp.created`.
- **Canal SFTP:** transferencia de archivos de instrucciones de pago a servidores SFTP por empresa (JSch + Spring Integration SFTP).
- **Patrón Outbox para OTP:** persistencia en MongoDB + scheduler de reintento cada 5 segundos con límite de 50 eventos por ciclo.
- **Plantillas de email:** motor Mustache/Thymeleaf con plantillas almacenadas en MySQL (`messages_templates`).
- **Endpoint REST `POST /notificarCorreo`:** envío síncrono de correos protegido por JWT.
- **Seguridad JWT:** filtro `JwtAuthFilter` stateless, autorización por roles mediante `@PreAuthorize` y `RoleEvaluator`.
- **Estándar corporativo de respuestas HTTP:** `GlobalExceptionHandler` con `ApiErrorResponse` para 400, 401, 403, 422 y 500.
- **Arquitectura hexagonal:** 13 reglas ArchUnit verificando separación de capas y ausencia de ciclos.
- **Tests unitarios Spock:** cobertura de líneas >= 99% con Spock Framework 2.4 (Groovy 4.0).
- **Calidad estática:** Checkstyle, PMD y SpotBugs con 0 violaciones.
- **Swagger / OpenAPI 3:** documentación interactiva con SpringDoc 2.8.3, servidores configurados para local, dev, QA y producción.
- **Docker multi-stage:** imagen optimizada con JRE Alpine y usuario no root (`appuser`).
- **Health probes:** liveness y readiness via Spring Boot Actuator para compatibilidad con Kubernetes.
- **Tests de rendimiento JMeter:** tres perfiles (smoke, carga sostenida, concurrencia).
- **Colección Postman:** smoke tests para `POST /notificarCorreo`.
- **Migración Maven → Gradle:** sistema de build con `build.gradle`, `test.gradle` y `coverage.gradle`.

---

> **Licencia:** Uso interno — Plataforma Factoring  
> **Contacto:** relativismofisico@gmail.com