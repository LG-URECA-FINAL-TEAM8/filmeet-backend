package com.ureca.filmeet.domain.auth.service;

import com.ureca.filmeet.domain.auth.dto.request.LoginRequest;
import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import com.ureca.filmeet.global.exception.InvalidPasswordException;
import com.ureca.filmeet.global.util.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdPwAuthenticationService {

    private final UserQueryService userQueryService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public TokenResponse authenticate(LoginRequest request) {
        User user = userQueryService.findByUsername(request.username());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException("Password is not matched");
        }

        // Access/Refresh Token 생성
        return tokenService.generateTokens(
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getRole().getAuthorities())
        );
    }
}
