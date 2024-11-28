package com.ureca.filmeet.domain.auth.controller.query;

import com.ureca.filmeet.domain.auth.dto.request.LoginRequest;
import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.auth.service.IdPwAuthenticationService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.util.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthQueryController {

    private final IdPwAuthenticationService idPwAuthenticationService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        TokenResponse tokens = idPwAuthenticationService.authenticate(request);
        return ApiResponse.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 접두사 제거
        String token = refreshToken.replace("Bearer ", "");

        // 새 Access/Refresh Token 생성
        TokenResponse tokens = tokenService.refreshAccessToken(token);

        return ApiResponse.ok(tokens);
    }
}
