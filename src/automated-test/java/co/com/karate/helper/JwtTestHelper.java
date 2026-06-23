package co.com.karate.helper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Genera JWT para los tests funcionales de Karate.
 * Llamado desde karate-config.js vía Java.type().
 */
public final class JwtTestHelper {

    private JwtTestHelper() {}

    public static String generateToken(String username, String rol, String base64Secret, int expirationSeconds) {
        SecretKey key = buildKey(base64Secret);
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationSeconds * 1000L))
                .signWith(key)
                .compact();
    }

    public static String generateExpiredToken(String username, String rol, String base64Secret) {
        SecretKey key = buildKey(base64Secret);
        long past = System.currentTimeMillis() - 7200_000L;
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(new Date(past - 3600_000L))
                .expiration(new Date(past))
                .signWith(key)
                .compact();
    }

    public static String generateTokenWithoutRolClaim(String username, String base64Secret) {
        SecretKey key = buildKey(base64Secret);
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + 300_000L))
                .signWith(key)
                .compact();
    }

    private static SecretKey buildKey(String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}