package co.com.email.security.jwt

import co.com.email.exception.dto.ApiErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

import java.io.PrintWriter
import java.io.StringWriter

class JwtAuthFilterSpec extends Specification {

    JwtValidator jwtValidator = Mock()
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
    JwtAuthFilter filter = new JwtAuthFilter(jwtValidator, objectMapper)

    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()
    FilterChain filterChain = Mock()
    StringWriter responseBody = new StringWriter()
    PrintWriter writer = new PrintWriter(responseBody)

    def setup() {
        SecurityContextHolder.clearContext()
        response.writer >> writer
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "sin header Authorization continúa sin autenticar"() {
        given:
        request.getHeader(HttpHeaders.AUTHORIZATION) >> null

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        0 * jwtValidator.validateAndExtract(_)
    }

    def "header que no empieza con Bearer continúa sin autenticar"() {
        given:
        request.getHeader(HttpHeaders.AUTHORIZATION) >> "Basic dXNlcjpwYXNz"

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        0 * jwtValidator.validateAndExtract(_)
    }

    def "token válido establece autenticación en SecurityContext"() {
        given:
        Claims claims = Mock()
        claims.getSubject() >> "jperez"
        claims.get("rol") >> "ADMINISTRADOR"
        request.getHeader(HttpHeaders.AUTHORIZATION) >> "Bearer token-valido"
        jwtValidator.validateAndExtract("token-valido") >> claims

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        SecurityContextHolder.context.authentication != null
        SecurityContextHolder.context.authentication.name == "jperez"
    }

    def "token expirado responde 401 con código TOKEN_EXPIRED"() {
        given:
        request.getHeader(HttpHeaders.AUTHORIZATION) >> "Bearer token-expirado"
        request.getMethod() >> "POST"
        request.getRequestURI() >> "/api/test"
        jwtValidator.validateAndExtract("token-expirado") >> {
            throw new ExpiredJwtException(null, null, "Token expirado")
        }

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * response.setStatus(401)
        0 * filterChain.doFilter(_, _)
        writer.flush()
        responseBody.toString().contains("TOKEN_EXPIRED")
    }

    def "token inválido responde 401 con código TOKEN_INVALID"() {
        given:
        request.getHeader(HttpHeaders.AUTHORIZATION) >> "Bearer token-invalido"
        request.getMethod() >> "GET"
        request.getRequestURI() >> "/api/test"
        jwtValidator.validateAndExtract("token-invalido") >> {
            throw new JwtException("Firma inválida")
        }

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * response.setStatus(401)
        0 * filterChain.doFilter(_, _)
        writer.flush()
        responseBody.toString().contains("TOKEN_INVALID")
    }

    def "token válido no sobreescribe autenticación existente"() {
        given:
        Claims claims = Mock()
        claims.getSubject() >> "jperez"
        claims.get("rol") >> "ADMINISTRADOR"
        request.getHeader(HttpHeaders.AUTHORIZATION) >> "Bearer token-valido"

        def existingAuth = Mock(org.springframework.security.core.Authentication)
        SecurityContextHolder.context.setAuthentication(existingAuth)
        jwtValidator.validateAndExtract("token-valido") >> claims

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        SecurityContextHolder.context.authentication == existingAuth
    }
}