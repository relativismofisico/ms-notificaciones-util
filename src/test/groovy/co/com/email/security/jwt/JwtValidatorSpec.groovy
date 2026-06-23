package co.com.email.security.jwt

import co.com.email.security.config.SecurityProperties
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import spock.lang.Specification

import javax.crypto.SecretKey
import java.time.Instant
import java.util.Date

class JwtValidatorSpec extends Specification {

    static final String SECRET_BASE64 = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLW11c3QtYmUtbG9uZy1lbm91Z2g="
    static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET_BASE64))

    JwtValidator validator

    def setup() {
        SecurityProperties props = new SecurityProperties()
        props.jwt = new SecurityProperties.Jwt()
        props.jwt.secret = SECRET_BASE64
        validator = new JwtValidator(props)
    }

    String buildToken(String username, String rol, Date expiry) {
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact()
    }

    def "validateAndExtract retorna claims con subject correcto para token válido"() {
        given:
        def expiry = Date.from(Instant.now().plusSeconds(3600))
        def token = buildToken("jperez", "ADMINISTRADOR", expiry)

        when:
        def claims = validator.validateAndExtract(token)

        then:
        claims.getSubject() == "jperez"
        claims.get("rol") == "ADMINISTRADOR"
    }

    def "validateAndExtract lanza ExpiredJwtException para token expirado"() {
        given:
        def expiry = Date.from(Instant.now().minusSeconds(3600))
        def token = buildToken("jperez", "ADMINISTRADOR", expiry)

        when:
        validator.validateAndExtract(token)

        then:
        thrown(ExpiredJwtException)
    }

    def "validateAndExtract lanza JwtException para token con firma inválida"() {
        given:
        SecretKey otherKey = Keys.hmacShaKeyFor(
                ("otra-clave-secreta-diferente-muy-larga-para-hmac-256").bytes)
        def token = Jwts.builder()
                .subject("hacker")
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(otherKey)
                .compact()

        when:
        validator.validateAndExtract(token)

        then:
        thrown(JwtException)
    }

    def "validateAndExtract lanza JwtException para token malformado"() {
        when:
        validator.validateAndExtract("esto.no.es.un.jwt.valido")

        then:
        thrown(JwtException)
    }
}