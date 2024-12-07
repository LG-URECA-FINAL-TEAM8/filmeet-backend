package com.ureca.filmeet.infra.firebase.controller;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.infra.firebase.dto.FCMTokenRequest;
import com.ureca.filmeet.infra.firebase.service.FCMTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications/token")
@RequiredArgsConstructor
public class FCMTokenController {
    private final FCMTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerToken(
            @RequestBody @Valid FCMTokenRequest request,
            @AuthenticationPrincipal User user
    ) {
        fcmTokenService.saveToken(user, request.token());
        return ApiResponse.ok(null);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> registerTokenById(
            @RequestBody @Valid FCMTokenRequest request,
            @PathVariable Long userId
    ) {
        fcmTokenService.saveToken(userId, request.token());
        return ApiResponse.ok(null);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeToken(
            @RequestBody @Valid FCMTokenRequest request,
            @AuthenticationPrincipal User user
    ) {
        fcmTokenService.removeToken(user, request.token());
        return ApiResponse.ok(null);
    }
}
