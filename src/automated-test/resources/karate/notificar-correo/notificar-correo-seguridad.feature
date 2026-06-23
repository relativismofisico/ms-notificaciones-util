@regression
Feature: POST /notificarCorreo - Seguridad JWT y Autorización
  Como API de notificaciones
  Quiero rechazar accesos sin autenticación o sin autorización
  Para proteger el endpoint de uso no autorizado

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * def payload = read('../data/correo-valido.json')
    * def schema401 = read('../schemas/api-error-401-schema.json')
    * def schema403 = read('../schemas/api-error-403-schema.json')

  # ── Sin JWT (401) ─────────────────────────────────────────────────────────

  @regression
  Scenario: Sin header Authorization - debe retornar 401
    Given path '/notificarCorreo'
    And request payload
    When method POST
    Then status 401
    And match response == schema401

  @regression
  Scenario: Header Authorization vacío - debe retornar 401
    Given path '/notificarCorreo'
    And header Authorization = ''
    And request payload
    When method POST
    Then status 401

  @regression
  Scenario: Header Authorization sin prefijo Bearer - debe retornar 401
    Given path '/notificarCorreo'
    And header Authorization = tokenAdmin
    And request payload
    When method POST
    Then status 401

  # ── JWT inválido (401 code: TOKEN_INVALID) ────────────────────────────────

  @regression
  Scenario: JWT con firma inválida (modificado) - debe retornar 401 con code TOKEN_INVALID
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWNrZXIiLCJyb2wiOiJBRE1JTklTVFJBRE9SIn0.firma_invalida_modificada'
    And request payload
    When method POST
    Then status 401
    And match response == schema401
    And match response.code == 'TOKEN_INVALID'

  @regression
  Scenario: JWT malformado (no es un JWT válido) - debe retornar 401 con code TOKEN_INVALID
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer esto.no.esunjwt'
    And request payload
    When method POST
    Then status 401
    And match response.code == 'TOKEN_INVALID'

  @regression
  Scenario: JWT con solo una parte (sin puntos) - debe retornar 401
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer tokensinpuntos'
    And request payload
    When method POST
    Then status 401
    And match response.code == 'TOKEN_INVALID'

  @regression
  Scenario: JWT con body alterado - debe retornar 401 con code TOKEN_INVALID
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWNrZXIiLCJyb2wiOiJBRE1JTklTVFJBRE9SIiwiZXhwIjo5OTk5OTk5OTk5fQ.diferente_firma'
    And request payload
    When method POST
    Then status 401
    And match response.code == 'TOKEN_INVALID'

  # ── JWT expirado (401 code: TOKEN_EXPIRED) ────────────────────────────────

  @regression
  Scenario: JWT expirado - debe retornar 401 con code TOKEN_EXPIRED
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenExpirado
    And request payload
    When method POST
    Then status 401
    And match response == schema401
    And match response.code == 'TOKEN_EXPIRED'
    And match response.message contains 'expirado'

  # ── JWT válido pero sin autorización (403) ────────────────────────────────

  @regression
  Scenario: JWT con rol SOPORTE (no en grupo administracion) - debe retornar 403
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenSinAcceso
    And request payload
    When method POST
    Then status 403
    And match response == schema403

  @regression
  Scenario: JWT válido pero sin claim rol - debe retornar 403
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenSinRolClaim
    And request payload
    When method POST
    Then status 403

  # ── Validación de schema en respuestas de seguridad ───────────────────────

  @regression
  Scenario: Respuesta 401 sin token cumple contrato ApiErrorResponse completo
    Given path '/notificarCorreo'
    And request payload
    When method POST
    Then status 401
    And match response.timestamp == '#notnull'
    And match response.status == 401
    And match response.error == 'Unauthorized'
    And match response.code == '#notnull'
    And match response.message == '#notnull'
    And match response.path == '/securityapi/notificarCorreo'
    And match response.fieldErrors == '#notpresent'

  @regression
  Scenario: Respuesta 403 cumple contrato ApiErrorResponse completo
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenSinAcceso
    And request payload
    When method POST
    Then status 403
    And match response.timestamp == '#notnull'
    And match response.status == 403
    And match response.path == '/securityapi/notificarCorreo'

  # ── Rutas públicas (permitAll) ────────────────────────────────────────────

  @regression @read-only @production-safe
  Scenario: Swagger UI accesible sin token
    Given url baseUrl.replace('/securityapi', '')
    And path '/securityapi/swagger-ui/index.html'
    When method GET
    Then status 200

  @regression @read-only @production-safe
  Scenario: OpenAPI docs accesible sin token
    Given url baseUrl.replace('/securityapi', '')
    And path '/securityapi/v3/api-docs'
    When method GET
    Then status 200
    And match response.info.title == '#notnull'

  # ── Combinación: payload inválido + sin token (prioridad: 401 antes de 400) ──

  @regression
  Scenario: Sin token y payload inválido - debe retornar 401 (autenticación primero)
    Given path '/notificarCorreo'
    And request {}
    When method POST
    Then status 401