package com.ureca.filmeet.domain.user.controller.query;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserQueryController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole(),
                "nickname", user.getNickname()
        ));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkDuplicateUsername(@RequestParam String username) {
        Boolean isAvailable = userQueryService.existsByUsername(username);
        return ApiResponse.ok(Map.of(
                "isAvailable", isAvailable
        ));
    }
}
