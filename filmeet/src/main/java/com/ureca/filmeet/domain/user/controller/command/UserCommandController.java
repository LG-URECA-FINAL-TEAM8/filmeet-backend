package com.ureca.filmeet.domain.user.controller.command;

import com.ureca.filmeet.domain.user.dto.request.UserSignUpRequest;
import com.ureca.filmeet.domain.user.dto.response.UserDetailResponse;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
