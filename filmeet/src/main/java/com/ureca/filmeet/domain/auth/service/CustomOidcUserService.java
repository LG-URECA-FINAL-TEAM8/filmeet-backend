package com.ureca.filmeet.domain.auth.service;

import com.ureca.filmeet.domain.auth.dto.CustomOidcUser;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserService extends OidcUserService {

    private final UserCommandService userCommandService;
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        log.info("oidcUser: {}", oidcUser);

        // ID 토큰과 claims 추출
        OidcIdToken idToken = oidcUser.getIdToken();
        Map<String, Object> claims = idToken.getClaims();

        // iss 값으로 Provider 확인
        String issuer = claims.get("iss").toString();
        Provider provider = Provider.fromIssuer(issuer);

        // Provider에서 nameKey 가져와서 사용자 이름 동적 추출
        String tmpProviderId = claims.get("sub").toString();
        String name = claims.getOrDefault(provider.getNameKey(), "name").toString();
        String picture = claims.getOrDefault("picture", "profile_image").toString();

        String providerId = provider.getName() + "_" + tmpProviderId;

        // 사용자 조회 또는 임시 사용자 생성
        User user = userRepository.findByUsername(providerId)
                .orElseGet(() -> userCommandService.createTemporaryUser(
                        providerId, name, provider, picture));

        // 권한 설정
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new CustomOidcUser(
                oidcUser.getAuthorities(),
                idToken,
                oidcUser.getUserInfo(),
                providerId
        );
    }
}
