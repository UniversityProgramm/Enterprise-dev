package com.lab3.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Генерация токена JWT
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        // Извлекаем ТОЛЬКО валидные роли (начинающиеся с ROLE_)
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_")) // ← ФИЛЬТРУЕМ ЛИШНИЕ РОЛИ!
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Получение имени пользователя из токена
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Получение ролей из токена
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("authorities", String.class);
    }

    /**
     * Проверка валидности токена
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            System.err.println("Недействительная подпись JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Недействительный токен JWT: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Срок действия токена JWT истек: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Токен JWT не поддерживается: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims строка пуста: " + e.getMessage());
        }
        return false;
    }

    /**
     * Получение ключа для подписи
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}