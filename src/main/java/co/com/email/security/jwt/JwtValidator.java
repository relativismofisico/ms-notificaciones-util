package co.com.email.security.jwt;

import co.com.email.security.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Valida y extrae claims de los JWT emitidos por ms-security.
 *
 * El token usa:
 * - Firma HMAC con clave compartida (misma key que JwtProvider de ms-security)
 * - Claim "rol" con valor string simple (ej: "ADMINISTRADOR")
 * - Subject = username del usuario
 *
 * jjwt valida automáticamente la firma y la expiración al parsear.
 * ExpiredJwtException y JwtException se propagan al JwtAuthFilter.
 */
@Component
public class JwtValidator {

    private final SecretKey signKey;

    public JwtValidator(SecurityProperties securityProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(securityProperties.getJwt().getSecret());
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims validateAndExtract(String token) {
        return Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}