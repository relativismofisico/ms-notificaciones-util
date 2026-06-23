package co.com.email.security.jwt;

import co.com.email.security.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtValidatorTest {

    private static final String SECRET =
            "AF84F1FGllNpNnLG055fdg5hGHJK4KGG5VH5TR5J05JFGGDFDGXVV545J4505G666JFGF2mMY95y";

    private JwtValidator jwtValidator;

    @BeforeEach
    void setUp() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setSecret(SECRET);
        jwtValidator = new JwtValidator(properties);
    }

    @Test
    void debeExtraerClaimsDeTokenValido() {
        String token = generarToken("usuario1", "ADMINISTRADOR", 3_600_000L);

        Claims claims = jwtValidator.validateAndExtract(token);

        assertThat(claims.getSubject()).isEqualTo("usuario1");
        assertThat(claims.get("rol")).isEqualTo("ADMINISTRADOR");
    }

    @Test
    void debeLanzarExpiredJwtExceptionParaTokenExpirado() {
        String token = generarToken("usuario1", "EMPRESA", -1_000L);

        assertThatThrownBy(() -> jwtValidator.validateAndExtract(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void debeLanzarJwtExceptionParaTokenManipulado() {
        String token = generarToken("usuario1", "OPERARIO", 3_600_000L) + "manipulado";

        assertThatThrownBy(() -> jwtValidator.validateAndExtract(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void debeLanzarJwtExceptionParaTokenConFirmaDistinta() {
        String otroSecreto = "ZZZ4F1FGllNpNnLG055fdg5hGHJK4KGG5VH5TR5J05JFGGDFDGXVV545J4505G666JFGF2mMY95y";
        byte[] otroKeyBytes = Decoders.BASE64.decode(otroSecreto);
        SecretKey otraKey = Keys.hmacShaKeyFor(otroKeyBytes);

        String tokenConOtraFirma = Jwts.builder()
                .subject("usuario1")
                .claim("rol", "FONDEADOR")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(otraKey)
                .compact();

        assertThatThrownBy(() -> jwtValidator.validateAndExtract(tokenConOtraFirma))
                .isInstanceOf(JwtException.class);
    }

    private String generarToken(String subject, String rol, long expirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(subject)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }
}