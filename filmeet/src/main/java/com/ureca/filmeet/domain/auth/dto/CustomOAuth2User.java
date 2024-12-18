package com.ureca.filmeet.domain.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User implements CustomUser {

    private final String providerId;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String providerId) {
        super(authorities, attributes, nameAttributeKey);
        this.providerId = providerId;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }
}
