@regression
Feature: POST /notificarCorreo - Validaciones de payload
  Como API de notificaciones
  Quiero rechazar payloads con datos inválidos o faltantes
  Para garantizar la integridad del contrato

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * header Authorization = 'Bearer ' + tokenAdmin
    * def schemaError400 = read('../schemas/api-error-400-schema.json')
    * def schemaError422 = read('../schemas/api-error-422-schema.json')
    * def schemaFieldError = read('../schemas/field-error-schema.json')

  # ── Bean Validation (400) ─────────────────────────────────────────────────

  @regression
  Scenario: Omitir correoRemitente obligatorio - debe retornar 400
    Given path '/notificarCorreo'
    And request read('../data/correo-sin-remitente.json')
    When method POST
    Then status 400
    And match response == schemaError400
    And match response.fieldErrors[0].field == 'correoRemitente'

  @regression
  Scenario: Omitir asunto obligatorio - debe retornar 400
    Given path '/notificarCorreo'
    And request read('../data/correo-sin-asunto.json')
    When method POST
    Then status 400
    And match response == schemaError400
    And match response.fieldErrors[0].field == 'asunto'

  @regression
  Scenario: Omitir destinatarios obligatorio - debe retornar 400
    Given path '/notificarCorreo'
    And request read('../data/correo-sin-destinatarios.json')
    When method POST
    Then status 400
    And match response == schemaError400
    And match response.fieldErrors[0].field == 'destinatarios'

  @regression
  Scenario: correoRemitente vacío (string vacío) - debe retornar 400
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "",
        "asunto": "Test",
        "destinatarios": ["qa-destinatario@empresa.com"]
      }
      """
    When method POST
    Then status 400
    And match response == schemaError400
    And match response.fieldErrors[*].field contains 'correoRemitente'

  @regression
  Scenario: asunto vacío - debe retornar 400
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "",
        "destinatarios": ["qa-destinatario@empresa.com"]
      }
      """
    When method POST
    Then status 400
    And match response == schemaError400
    And match response.fieldErrors[*].field contains 'asunto'

  @regression
  Scenario: destinatarios vacío (lista vacía) - debe retornar 400
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test",
        "destinatarios": []
      }
      """
    When method POST
    Then status 400
    And match response == schemaError400

  @regression
  Scenario: Múltiples campos obligatorios omitidos - todos aparecen en fieldErrors
    Given path '/notificarCorreo'
    And request {}
    When method POST
    Then status 400
    And match response == schemaError400
    And match response.fieldErrors == '#[_ > 1]'

  @regression
  Scenario: Payload completamente vacío (body nulo) - debe retornar 400
    Given path '/notificarCorreo'
    And request ''
    When method POST
    Then status 400

  # ── Validación de negocio (422) ───────────────────────────────────────────

  @regression
  Scenario: correoRemitente con formato inválido - debe retornar 422
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "no-es-un-email",
        "asunto": "Test email inválido",
        "destinatarios": ["qa-destinatario@empresa.com"]
      }
      """
    When method POST
    Then status 422
    And match response == schemaError422
    And match response.code == 'EMAIL_VALIDATION_ERROR'

  @regression
  Scenario: correoRemitente con formato parcialmente inválido - debe retornar 422
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "usuario@",
        "asunto": "Test email inválido parcial",
        "destinatarios": ["qa-destinatario@empresa.com"]
      }
      """
    When method POST
    Then status 422
    And match response.code == 'EMAIL_VALIDATION_ERROR'

  @regression
  Scenario: correoRemitente con formato solo dominio - debe retornar 422
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "@empresa.com",
        "asunto": "Test email solo dominio",
        "destinatarios": ["qa-destinatario@empresa.com"]
      }
      """
    When method POST
    Then status 422
    And match response.code == 'EMAIL_VALIDATION_ERROR'

  @regression
  Scenario: Todos los destinatarios con formato inválido sin CC ni BCC - debe retornar 422
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test todos destinatarios inválidos",
        "destinatarios": ["no-es-email", "tampoco-email", "otro-invalido"]
      }
      """
    When method POST
    Then status 422
    And match response == schemaError422
    And match response.message contains 'destinatarios'

  @regression
  Scenario: destinatarios con lista de emails inválidos y CC también inválido - debe retornar 422
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test sin válidos en ninguna lista",
        "destinatarios": ["invalido1"],
        "destinatariosCc": ["invalido2"],
        "destinatariosBcc": ["invalido3"]
      }
      """
    When method POST
    Then status 422
    And match response.code == 'EMAIL_VALIDATION_ERROR'

  # ── Validación de schema en errores ──────────────────────────────────────

  @regression
  Scenario: Respuesta 400 cumple contrato ApiErrorResponse - validación estricta de schema
    Given path '/notificarCorreo'
    And request {}
    When method POST
    Then status 400
    And match response.timestamp == '#notnull'
    And match response.status == 400
    And match response.error == 'Bad Request'
    And match response.code == 'VALIDATION_ERROR'
    And match response.message == '#notnull'
    And match response.path == '/securityapi/notificarCorreo'
    And match each response.fieldErrors == schemaFieldError

  @regression
  Scenario: Respuesta 422 cumple contrato ApiErrorResponse
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "email-invalido",
        "asunto": "Test 422",
        "destinatarios": ["qa@empresa.com"]
      }
      """
    When method POST
    Then status 422
    And match response.timestamp == '#notnull'
    And match response.status == 422
    And match response.error == 'Unprocessable Entity'
    And match response.code == 'EMAIL_VALIDATION_ERROR'
    And match response.path == '/securityapi/notificarCorreo'
    And match response.fieldErrors == '#notpresent'

  # ── Casos edge con tipos de datos incorrectos ─────────────────────────────

  @regression
  Scenario: destinatarios como string en lugar de lista - debe retornar 400
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test tipo incorrecto",
        "destinatarios": "qa-destinatario@empresa.com"
      }
      """
    When method POST
    Then status 400

  @regression
  Scenario Outline: Múltiples campos con formato inválido - todos retornan 400
    Given path '/notificarCorreo'
    And request <payload>
    When method POST
    Then status <expectedStatus>
    And match response.code == '<expectedCode>'

    Examples:
      | descripcion                     | payload                                                                                                                           | expectedStatus | expectedCode           |
      | sin correoRemitente             | { "asunto": "Test", "destinatarios": ["qa@empresa.com"] }                                                                        | 400            | VALIDATION_ERROR       |
      | correoRemitente formato invalido | { "correoRemitente": "malformado", "asunto": "Test", "destinatarios": ["qa@empresa.com"] }                                       | 422            | EMAIL_VALIDATION_ERROR |
      | sin asunto                      | { "correoRemitente": "qa@empresa.com", "destinatarios": ["qa@empresa.com"] }                                                     | 400            | VALIDATION_ERROR       |
      | todos destinatarios invalidos   | { "correoRemitente": "qa@empresa.com", "asunto": "Test", "destinatarios": ["invalido"] }                                         | 422            | EMAIL_VALIDATION_ERROR |