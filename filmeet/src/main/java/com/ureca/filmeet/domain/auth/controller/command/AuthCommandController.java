package com.ureca.filmeet.domain.auth.controller.command;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.util.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthCommandController {

    private final TokenService tokenService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user) {
        tokenService.invalidateTokens(user.getUsername());
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> admin(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(user.getRole());
    }
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') and hasAuthority()")
    public ResponseEntity<?> user(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(user.getRole());
    }
}
