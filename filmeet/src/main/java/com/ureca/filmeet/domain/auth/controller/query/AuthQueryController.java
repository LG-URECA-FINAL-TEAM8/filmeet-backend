package com.ureca.filmeet.domain.auth.controller.query;

import com.ureca.filmeet.domain.auth.dto.request.LoginRequest;
import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.auth.service.IdPwAuthenticationService;
import com.ureca.filmeet.global.util.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthQueryController {

    private final IdPwAuthenticationService idPwAuthenticationService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        TokenResponse tokens = idPwAuthenticationService.authenticate(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 접두사 제거
        String token = refreshToken.replace("Bearer ", "");

        // 새 Access/Refresh Token 생성
        TokenResponse tokens = tokenService.refreshAccessToken(token);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/google")
    public ResponseEntity<?> debugGoogleResponse(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        // Google 토큰 엔드포인트로 Access Token 요청
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> tokenRequest = Map.of(
                "code", code,
                "client_id", "71534606368-c6tfi74urmqo4sj8lsiq8d6kvaa19qsu.apps.googleusercontent.com",
                "client_secret", "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",
                "redirect_uri", "http://localhost:8080/login/oauth2/code/google",
                "grant_type", "authorization_code"
        );

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                tokenRequest,
                Map.class
        );

        return ResponseEntity.ok(tokenResponse.getBody());
    }
}
