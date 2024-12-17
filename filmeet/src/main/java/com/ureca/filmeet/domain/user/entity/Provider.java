package com.ureca.filmeet.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Provider {
    // OIDC Providers
    GOOGLE("google", true, "https://accounts.google.com", "name"),
    KAKAO("kakao", true, "https://kauth.kakao.com", "nickname"),

    // OAuth2 Providers
    NAVER("naver", false, "https://nid.naver.com", "name");

    private final String name;        // Provider 이름
    private final boolean isOidc;     // OIDC 여부
    private final String issuer;      // iss 값
    private final String nameKey;     // 사용자 이름 키

    public static Provider fromIssuer(String issuer) {
        return Arrays.stream(values())
                .filter(provider -> issuer.contains(provider.getIssuer()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching provider for issuer: " + issuer));
    }

    public static Optional<Provider> fromName(String name) {
        return Arrays.stream(values())
                .filter(provider -> provider.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}

