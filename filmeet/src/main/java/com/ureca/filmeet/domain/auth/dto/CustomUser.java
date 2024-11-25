package com.ureca.filmeet.domain.auth.dto;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface CustomUser extends OAuth2User {
    String getProviderId();
}
