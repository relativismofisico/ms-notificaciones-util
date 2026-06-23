# Microservicio - ms-notificaciones-util
BIAN <span style="color:#ff1338;">**area - path ms**</span>

Microservicio utilitario encargado del envio de notificaciones a traves de email o sms de cada uno de los procesos de una plataforma

1. [Propiedad](/README.md#1-propiedad)
2. [Operaciones](/README.md#2-operaciones)
3. [Especificación Técnica](/README.md#3-especificacin-tcnica)
4. [Dependencias](/README.md#4-dependencias)
5. [Eventos](/README.md#5-eventos)
6. [Ciclo de Vida](/README.md#6-ciclo-de-vida)
7. [Log de cambios](/README.md#7-log-de-cambios)


## 1. Propiedad
Esta sección detalla quién es el responsable del ciclo de vida del microservicio.

### 1.1. Dueño de Negocio
Unidad de negocio responsable y dueña del microservicio.

- Unidad de Negocio: 
- Responsable: 
    + Contacto: 
    + Teléfono: 

### 1.2. Dueño Técnico
Unidad técnica referente del microservicio.

- Unidad Técnica: 
- Responsable: 
    + Contacto: 
    + Teléfono: 

### 1.3. Célula Dueña
Célula responsable y dueña del microservicio.

- Célula: 

## 2. Operaciones
Detalla las operaciones a **nivel de negocio** que se pueden explotar a través del microservicio.

| Metodo | Operación | Descripción Capacidad                                                                                                                                                                    |
|---------------------------------------------|--------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <span style="color:#00cf8b">**POST**</span> | /sendHTMLEmail | Metodo encargado del envio de un email a traves del servicio de aws ses utilizando una plantilla propia del ms                                                                           |

## 3. Especificación Técnica
Detalle técnico del microservicio provisto por especificación Swagger.

```yaml

```

## 4. Dependencias
Detalla los sistemas y otros microservicios que son necesarios para el funcionamiento de este microservicio.

### 4.1. Microservicios

| Dependencia | Método | Operación | Descripción de la dependencia |
|-----------------------------------|---------------------------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------------------------|
|  | <span style="color:#3fb1ff">**GET**</span> |  |  |

### 4.2. Repositorio de Datos

| Repositorio | Base de datos | Descripción |
|-------------|---------------|-------------------------|
|  |  |   |

### 4.3. Backends

| Dependencia | Descripción de la dependencia | Operación |
|-------------|-------------------------------|-----------|
| N/A | N/A | N/A |

## 5. Eventos
NA

## 6. Ciclo de Vida
En esta sección se especifica el procedimiento para disponibilizar el microservicio.

### 6.1. Compilación
Detalla la forma en que el microsBIANervicio debe ser compilado:

```shell
./mv build
```

### 6.2. Configuraciones
Detalla las configuraciones necesarias para que el microservicio pueda operar:

#### 6.2.1. Propiedades

```yaml
Url para el config file
```

| Clave | Valor | Descripción | 
|------------------------------------|--------------------|---------------------------------------------------------------------------------------|
|  |  | |

#### 6.2.2. Secretos
| Clave | Descripción | 
|-------|-------------|
| N/A | N/A |

### 6.3. Ejecución

#### 6.3.1. Local
Especifica la forma en que se ejecuta la aplicación localmente.

```shell
./mv bootRun -SPRING_PROFILES_ACTIVE='<ambiente>' -CONTEXT_PATH='/path/ms-notificaciones-util/<MS_VERSION>' -SPRING_CLOUD_CONFIG_URI='http://' - VAULT_SCHEME='https' -VAULT_HOST='' -VAULT_PORT='8080' -VAULT_TOKEN='<VAULT_TOKEN>' -e VAULT_AUTHENTICATION='TOKEN' -VAULT_TRUST_STORE='file:/' -VAULT_TRUST_STORE_PWD='<VAULT_TRUST_STORE_PWD>' -MS_VERSION='<MS_VERSION>' -8080:8080 -name=ms-notificaciones-util
```

Descripción de variables de entorno:
- SPRING_PROFILES_ACTIVE: Perfil a utilizar con los siguientes valores por ambiente:
+ Integración: integracion
+ QA: qa
+ Producción: produccion

#### 6.3.2. Recursos Utilizados
Detalla los recursos utilizados por una instancia de un contenedor docker.

| Recurso | Requerido | Justificación |
|------------|-------------------|---------------|
| CPU | Valor por defecto | |
| Memoria | Valor por defecto | |
| Storage | Valor por defecto | |
| Throughput | Valor por defecto | |

#### 6.3.3. Monitoreo
(https://)

## 7. Log de Cambios

### Versión 1.0.0 (2025-01-15)
- Versión inicial del ms con el método validarProspecto
- HDU: ###URl de Jira
- OC: No de orden de cambio

# Copyright
Copyright (c). Todos los derechos reservados