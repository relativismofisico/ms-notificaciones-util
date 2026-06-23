@regression @smoke
Feature: POST /notificarCorreo - Happy Path
  Como consumidor del API de notificaciones
  Quiero enviar correos SMTP con datos válidos
  Para recibir respuesta 204 No Content

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * header Authorization = 'Bearer ' + tokenAdmin

  @smoke @regression @production-safe
  Scenario: Enviar correo mínimo válido - debe retornar 204
    Given path '/notificarCorreo'
    And request read('../data/correo-valido.json')
    When method POST
    Then status 204
    And match response == ''

  @regression
  Scenario: Enviar correo completo con CC, BCC y adjunto - debe retornar 204
    Given path '/notificarCorreo'
    And request read('../data/correo-completo.json')
    When method POST
    Then status 204
    And match response == ''

  @regression
  Scenario: Enviar correo con rol EMPRESA - debe retornar 204
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenEmpresa
    And request read('../data/correo-valido.json')
    When method POST
    Then status 204

  @regression
  Scenario: Enviar correo con rol OPERARIO - debe retornar 204
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenOperario
    And request read('../data/correo-valido.json')
    When method POST
    Then status 204

  @regression
  Scenario: Enviar correo con rol FONDEADOR - debe retornar 204
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + tokenFondeador
    And request read('../data/correo-valido.json')
    When method POST
    Then status 204

  @regression
  Scenario: Enviar correo sin nombreRemitente - nombreRemitente es opcional, debe retornar 204
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test sin nombreRemitente",
        "destinatarios": ["qa-destinatario@empresa.com"],
        "cuerpoTexto": "Prueba sin campo opcional nombreRemitente"
      }
      """
    When method POST
    Then status 204

  @regression
  Scenario: Enviar correo solo con destinatariosCc (sin destinatarios To) - negocio filtra emails inválidos pero Bcc o Cc válidos son suficientes
    Given path '/notificarCorreo'
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test solo CC",
        "destinatarios": ["email-invalido"],
        "destinatariosCc": ["qa-cc@empresa.com"],
        "cuerpoTexto": "Prueba con destinatario To inválido filtrado, CC válido"
      }
      """
    When method POST
    Then status 204

  @regression
  Scenario: Enviar correo con múltiples destinatarios dentro del límite permitido
    Given path '/notificarCorreo'
    And def destinatariosLista = (function(){ var list = []; for(var i=1;i<=10;i++){ list.push('qa-' + i + '@empresa.com'); } return list; })()
    And request
      """
      {
        "correoRemitente": "qa-auto@empresa.com",
        "asunto": "Test múltiples destinatarios",
        "destinatarios": "#(destinatariosLista)",
        "cuerpoTexto": "Prueba con 10 destinatarios"
      }
      """
    When method POST
    Then status 204

  @regression
  Scenario Outline: Todos los roles del grupo administración deben tener acceso
    Given path '/notificarCorreo'
    And header Authorization = 'Bearer ' + <token>
    And request read('../data/correo-valido.json')
    When method POST
    Then status 204

    Examples:
      | descripcion   | token          |
      | ADMINISTRADOR | tokenAdmin     |
      | EMPRESA       | tokenEmpresa   |
      | OPERARIO      | tokenOperario  |
      | FONDEADOR     | tokenFondeador |