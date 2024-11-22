package com.ureca.filmeet.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Provider {
    //OIDC Provider
    GOOGLE("google", true),

    //OAuth2 Provider
    NAVER("naver", false);

    private final String name;
    private final boolean isOidcProvider;

    public static Optional<Provider> fromName(String name) {
        return Arrays.stream(values())
                .filter(provider -> provider.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
