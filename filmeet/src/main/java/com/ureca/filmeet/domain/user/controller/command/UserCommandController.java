package com.ureca.filmeet.domain.user.controller.command;

import com.ureca.filmeet.domain.user.dto.request.UserSignUpRequest;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        User newUser = userCommandService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered with ID: " + newUser.getUsername());
    }
}
