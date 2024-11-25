package com.ureca.filmeet.global.util.jwt;

import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.global.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenResponse generateTokens(Authentication authentication) {
        String username = authentication.getName();

        // Role 추출
        Role role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_")) // Role 권한 필터링
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        // Access/Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(username, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // Refresh Token Redis 저장
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + username,
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidity(),
                TimeUnit.MINUTES
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse generateTokens(String username, Role role) {

        // Access/Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(username, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // Refresh Token Redis 저장
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + username,
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidity(),
                TimeUnit.MINUTES
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationException("Invalid Refresh Token");
        }

        // 사용자 이름 추출
        String username = jwtTokenProvider.getUsername(refreshToken);

        // Redis에서 저장된 Refresh Token 가져오기
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AuthenticationException("Refresh Token mismatch");
        }

        // 새로운 Access Token 및 Refresh Token 생성
        Role role = jwtTokenProvider.getRole(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(username, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

        // Redis에 새로운 Refresh Token 저장 (기존 Token 대체)
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + username,
                newRefreshToken,
                jwtTokenProvider.getRefreshTokenValidity(),
                TimeUnit.MINUTES
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void invalidateTokens(String username) {
        String redisKey = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(redisKey);
    }
}
