package co.com.email.security.jwt;

import co.com.email.security.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtValidator {

    private final SecurityProperties securityProperties;

    public Claims validateAndExtract(String token) {
        return Jwts.parser()
                .verifyWith(buildSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey buildSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(securityProperties.getJwt().getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}