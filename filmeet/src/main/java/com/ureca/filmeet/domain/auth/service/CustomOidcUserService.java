package com.ureca.filmeet.domain.auth.service;

import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        OidcIdToken idToken = oidcUser.getIdToken();
        Map<String, Object> claims = idToken.getClaims();

        String issuer = claims.get("iss").toString();
        String tmpProviderId = claims.get("sub").toString();
        String email = claims.get("email").toString();
        String name = claims.get("name").toString();

        Provider provider = Arrays.stream(Provider.values())
                .filter(Provider::isOidcProvider)
                .filter(cur -> issuer.contains(cur.getName()))
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationException("No matching issuers found" + issuer));

        String providerId = provider.getName() + "_" + tmpProviderId;


        User user = userRepository.findByUsername(providerId)
                .orElseGet(() -> userCommandService.createTemporaryUser(providerId, name, Provider.GOOGLE));

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new DefaultOidcUser(authorities, idToken, oidcUser.getUserInfo());
    }
}
