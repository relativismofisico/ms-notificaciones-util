// jwt-generator.groovy
// Ejecutado en setUp Thread Group de JMeter via JSR223Sampler
// Genera tokens JWT HMAC-SHA256 sin dependencias externas (solo JVM standard)
// Los tokens se almacenan como JMeter Properties compartidas entre todos los hilos

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

def buildJWT = { String username, String rol, byte[] secretBytes, int expirySeconds ->
    def b64 = java.util.Base64.getUrlEncoder().withoutPadding()

    def header = '{"alg":"HS256","typ":"JWT"}'
    def hB64 = b64.encodeToString(header.getBytes("UTF-8"))

    long now = System.currentTimeMillis() / 1000
    long exp = now + expirySeconds
    def payload = "{\"sub\":\"${username}\",\"rol\":\"${rol}\",\"iat\":${now},\"exp\":${exp}}"
    def pB64 = b64.encodeToString(payload.getBytes("UTF-8"))

    def data = "${hB64}.${pB64}"
    def mac = Mac.getInstance("HmacSHA256")
    mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"))
    def sig = b64.encodeToString(mac.doFinal(data.getBytes("UTF-8")))

    return "${data}.${sig}"
}

def jwtSecret = props.get("jwt.secret")
if (!jwtSecret || jwtSecret.contains("CONFIGURAR_VIA_ENV")) {
    log.error("[JWT Generator] jwt.secret no configurado. Use -Djwt.secret=<base64-secret>")
    SampleResult.setSuccessful(false)
    SampleResult.setResponseMessage("ERROR: jwt.secret no configurado")
    return
}

def expirySeconds = Integer.parseInt(props.get("jwt.expiry.seconds") ?: "3600")
byte[] secretBytes = java.util.Base64.getDecoder().decode(jwtSecret)

def tokenAdmin     = buildJWT("qa-admin",   "ADMINISTRADOR", secretBytes, expirySeconds)
def tokenEmpresa   = buildJWT("qa-empresa", "EMPRESA",       secretBytes, expirySeconds)
def tokenSinAcceso = buildJWT("qa-soporte", "SOPORTE",       secretBytes, expirySeconds)

props.put("jwt.token.admin",     tokenAdmin)
props.put("jwt.token.empresa",   tokenEmpresa)
props.put("jwt.token.sinacceso", tokenSinAcceso)

log.info("[JWT Generator] Tokens generados. Ambiente: ${props.get('ambiente', 'dev')} | Expiry: ${expirySeconds}s")
SampleResult.setSuccessful(true)
SampleResult.setResponseMessage("Tokens JWT generados: ADMINISTRADOR, EMPRESA, SOPORTE")