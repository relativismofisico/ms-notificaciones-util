# Suite de Pruebas Funcionales - Karate Framework
## ms-notificaciones-util

---

## Matriz de Cobertura

| Escenario | Endpoint | HTTP | Tag | Archivo |
|---|---|---|---|---|
| Correo válido - happy path | POST /notificarCorreo | 204 | @smoke @regression @production-safe | happy-path.feature |
| Correo completo (CC, BCC, adjunto) | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Rol EMPRESA | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Rol OPERARIO | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Rol FONDEADOR | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Sin nombreRemitente (opcional) | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Destinatarios To inválidos, CC válido | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| 10 destinatarios (dentro del límite) | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Todos los roles autorizados (Scenario Outline) | POST /notificarCorreo | 204 | @regression | happy-path.feature |
| Sin correoRemitente | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| Sin asunto | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| Sin destinatarios | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| correoRemitente vacío | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| asunto vacío | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| destinatarios lista vacía | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| Múltiples campos omitidos | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| Body vacío | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| correoRemitente formato inválido | POST /notificarCorreo | 422 | @regression | validaciones.feature |
| correoRemitente parcialmente inválido | POST /notificarCorreo | 422 | @regression | validaciones.feature |
| correoRemitente solo dominio | POST /notificarCorreo | 422 | @regression | validaciones.feature |
| Todos destinatarios inválidos (sin CC/BCC) | POST /notificarCorreo | 422 | @regression | validaciones.feature |
| Sin válidos en ninguna lista | POST /notificarCorreo | 422 | @regression | validaciones.feature |
| Schema 400 estricto | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| Schema 422 estricto | POST /notificarCorreo | 422 | @regression | validaciones.feature |
| destinatarios tipo string | POST /notificarCorreo | 400 | @regression | validaciones.feature |
| Sin header Authorization | POST /notificarCorreo | 401 | @regression | seguridad.feature |
| Header Authorization vacío | POST /notificarCorreo | 401 | @regression | seguridad.feature |
| Sin prefijo Bearer | POST /notificarCorreo | 401 | @regression | seguridad.feature |
| JWT firma inválida | POST /notificarCorreo | 401 TOKEN_INVALID | @regression | seguridad.feature |
| JWT malformado | POST /notificarCorreo | 401 TOKEN_INVALID | @regression | seguridad.feature |
| JWT una sola parte | POST /notificarCorreo | 401 TOKEN_INVALID | @regression | seguridad.feature |
| JWT body alterado | POST /notificarCorreo | 401 TOKEN_INVALID | @regression | seguridad.feature |
| JWT expirado | POST /notificarCorreo | 401 TOKEN_EXPIRED | @regression | seguridad.feature |
| Rol SOPORTE (sin acceso) | POST /notificarCorreo | 403 | @regression | seguridad.feature |
| JWT sin claim rol | POST /notificarCorreo | 403 | @regression | seguridad.feature |
| Schema 401 estricto | POST /notificarCorreo | 401 | @regression | seguridad.feature |
| Schema 403 estricto | POST /notificarCorreo | 403 | @regression | seguridad.feature |
| Swagger UI público | GET /swagger-ui/index.html | 200 | @read-only @production-safe | seguridad.feature |
| OpenAPI docs público | GET /v3/api-docs | 200 | @read-only @production-safe | seguridad.feature |
| Sin token + payload inválido = 401 | POST /notificarCorreo | 401 | @regression | seguridad.feature |

**Total: 40 escenarios** | **Cobertura: 100% de paths del endpoint**

---

## Estructura de carpetas

```
src/automated-test/
├── java/co/com/karate/
│   ├── helper/
│   │   └── JwtTestHelper.java          # Generador de JWT para tests
│   └── runner/
│       ├── NotificacionesCorreoRunner.java  # Runner principal
│       ├── SmokeRunner.java                 # Solo @smoke
│       └── RegressionRunner.java            # Solo @regression
└── resources/
    ├── karate-config.js                # Configuración multiambiente
    ├── logback-test.xml                # Logging de tests
    └── karate/
        ├── notificar-correo/
        │   ├── notificar-correo-happy-path.feature
        │   ├── notificar-correo-validaciones.feature
        │   └── notificar-correo-seguridad.feature
        ├── schemas/
        │   ├── api-error-response-schema.json
        │   ├── api-error-400-schema.json
        │   ├── api-error-401-schema.json
        │   ├── api-error-403-schema.json
        │   ├── api-error-422-schema.json
        │   └── field-error-schema.json
        └── data/
            ├── correo-valido.json
            ├── correo-completo.json
            ├── correo-sin-remitente.json
            ├── correo-sin-asunto.json
            └── correo-sin-destinatarios.json
```

---

## Ejecución

### Prerrequisitos
- JDK 21+
- La aplicación corriendo en el ambiente correspondiente
- Variable `JWT_SECRET` configurada (en dev/qa usa el default)

### Comandos

```bash
# Smoke tests (ambiente dev - default)
./gradlew acceptanceTest

# Smoke tests en QA
AMBIENTE_PIPE=qa QA_BASE_URL=http://qa-server:8055/securityapi ./gradlew acceptanceTest

# Regression completo en QA
AMBIENTE_PIPE=qa ./gradlew acceptanceTest -Dkarate.options="--tags @regression"

# Solo producción (read-only + production-safe)
AMBIENTE_PIPE=prod PROD_BASE_URL=https://prod-server/securityapi JWT_SECRET=<secret> ./gradlew acceptanceTest
```

### Variables de entorno

| Variable | Requerida en | Descripción |
|---|---|---|
| `AMBIENTE_PIPE` | Siempre | `dev` \| `qa` \| `prod` |
| `JWT_SECRET` | prod | Secreto HMAC-SHA para firmar JWT |
| `QA_BASE_URL` | qa | URL base del servidor QA |
| `PROD_BASE_URL` | prod | URL base del servidor producción |

---

## Tags

| Tag | Descripción | Uso |
|---|---|---|
| `@smoke` | Validación rápida post-deploy (1 escenario happy path) | Post-deploy, PR |
| `@regression` | Suite completa de regresión | Merge a develop/main |
| `@read-only` | No modifica estado (GET) | Cualquier ambiente |
| `@production-safe` | Seguro para producción | Solo en prod |

En **producción**: solo se ejecutan `@production-safe`. Nunca se corren `@regression` a solas en prod.

---

## Reportes

Tras la ejecución se generan en `build/karate-reports/`:
- **HTML**: `build/karate-reports/html/index.html`
- **JUnit XML**: `build/karate-reports/xml/` (para Jenkins/GitLab/SonarQube)
- **Cucumber HTML**: `build/karate-reports/cucumber/feature-overview.html`

---

## CI/CD

| Pipeline | Archivo | Trigger |
|---|---|---|
| GitHub Actions | `.github/workflows/functional-tests.yml` | Push/PR a develop/main |
| Jenkins | `Jenkinsfile.functional` | Manual con parámetros |
| GitLab CI | `.gitlab-ci-functional.yml` | Push a develop/main |

**Credenciales nunca se hardcodean** — se leen de `secrets` (GitHub), `credentials()` (Jenkins) o variables de CI (GitLab).