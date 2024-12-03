package com.ureca.filmeet.domain.user.controller.command;

import com.ureca.filmeet.domain.user.dto.request.UpdatePreferenceRequest;
import com.ureca.filmeet.domain.user.dto.request.UserSignUpRequest;
import com.ureca.filmeet.domain.user.dto.response.UserDetailResponse;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserCommandController {
    private final UserCommandService userCommandService;


    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@RequestBody UserSignUpRequest request) {
        UserDetailResponse userDetailResponse = userCommandService.signUp(request);
        return ApiResponse.created(userDetailResponse);
    }

    @PostMapping("/update/profile")
    public ResponseEntity<?> updateProfileImage(@RequestParam String profileImage,
                                                @AuthenticationPrincipal User user) {
        userCommandService.UpdateProfileImage(profileImage, user);
        return ApiResponse.okWithoutData();
    }

    @PostMapping("/update/preference")
    public ResponseEntity<?> updatePreference(@RequestBody UpdatePreferenceRequest request,
                                              @AuthenticationPrincipal User user) {
        userCommandService.updatePreference(request, user);
        return ApiResponse.okWithoutData();
    }
}
