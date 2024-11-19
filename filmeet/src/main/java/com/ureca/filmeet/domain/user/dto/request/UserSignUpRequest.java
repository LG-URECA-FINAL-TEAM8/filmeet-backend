package com.ureca.filmeet.domain.user.dto.request;

public record UserSignUpRequest(String username,
                                String password,
                                String nickname) {
}