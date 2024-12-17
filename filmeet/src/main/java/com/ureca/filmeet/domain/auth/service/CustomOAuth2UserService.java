package com.ureca.filmeet.domain.auth.service;

import com.ureca.filmeet.domain.auth.dto.CustomOAuth2User;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserCommandService userCommandService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 사용자 정보 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // registrationId로 Provider 식별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.fromName(registrationId)
                .orElseThrow(() -> new OAuth2AuthenticationException("Unsupported OAuth2 Provider: " + registrationId));

        // 사용자 정보 매핑
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId;
        String name;
        String profileImage;

        switch (provider) {
            case NAVER -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                providerId = provider.getName() + "_" + response.get("id");
                name = (String) response.get("name");
                profileImage = (String) response.get("profile_image");
            }
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 Provider: " + registrationId);
        }

        User user = userRepository.findByUsername(providerId)
                .orElseGet(() -> userCommandService.createTemporaryUser(
                        providerId, name, Provider.NAVER, profileImage));


        // 권한 설정
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        Map<String, Object> copyAttributes = new HashMap<>(oAuth2User.getAttributes());
        copyAttributes.put("id", providerId);

        return new CustomOAuth2User(
                oAuth2User.getAuthorities(),
                copyAttributes,
                "id",
                providerId
        );
    }
}
