package com.ureca.filmeet.domain.auth.controller.command;

import com.ureca.filmeet.domain.auth.dto.request.LoginRequest;
import com.ureca.filmeet.domain.auth.dto.response.LoginResponse;
import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.auth.service.IdPwAuthenticationService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.util.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthCommandController {

    private final IdPwAuthenticationService idPwAuthenticationService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = idPwAuthenticationService.authenticate(request);
        return ApiResponse.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 접두사 제거
        String token = refreshToken.replace("Bearer ", "");

        // 새 Access/Refresh Token 생성
        TokenResponse tokens = tokenService.refreshAccessToken(token);
        return ApiResponse.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user) {
        log.info("user = {}", user);
        tokenService.invalidateTokens(user.getUsername());
        return ApiResponse.okWithoutData();
    }
}
