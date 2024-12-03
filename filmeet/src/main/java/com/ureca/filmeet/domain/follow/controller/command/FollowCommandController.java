package com.ureca.filmeet.domain.follow.controller.command;

import com.ureca.filmeet.domain.follow.service.command.FollowCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowCommandController {
    private final FollowCommandService followCommandService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user
    ) {
        followCommandService.follow(userId, user);
        return ApiResponse.created(null);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user
    ) {
        followCommandService.unfollow(userId, user);
        return ApiResponse.ok(null);
    }
}
