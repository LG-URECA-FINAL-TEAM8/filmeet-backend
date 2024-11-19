package com.ureca.filmeet.global.util.jwt;

import com.ureca.filmeet.domain.user.entity.Role;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity; // Access Token 유효시간 (분)
    private final long refreshTokenValidity; // Refresh Token 유효시간 (분)

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.validity.access}") long accessTokenValidity,
                            @Value("${jwt.validity.refresh}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(String username, Role role) {
        return createToken(username, role, accessTokenValidity);
    }

    public String createRefreshToken(String username) {
        return createToken(username, null, refreshTokenValidity);
    }

    private String createToken(String username, Role role, long validityMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(validityMinutes);

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(expiry))
                .signWith(key, SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role.name());
        }

        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Role getRole(String token) {
        String role = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(role);
    }

    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}