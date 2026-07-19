package com.code81.onlinestore.security;

import com.code81.onlinestore.entity.OwnerType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(Long id, String username, OwnerType ownerType, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);
        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("username", username)
                .claim("ownerType", ownerType.name())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    /**
     * Throws io.jsonwebtoken.JwtException (or a subclass, e.g. ExpiredJwtException)
     * if the token is invalid/expired - callers catch that, not this method.
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public AppUserPrincipal toPrincipal(Claims claims) {
        Long id = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        OwnerType ownerType = OwnerType.valueOf(claims.get("ownerType", String.class));
        String role = claims.get("role", String.class);
        return new AppUserPrincipal(id, username, ownerType, role);
    }
}
