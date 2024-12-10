package com.ureca.filmeet.global.security;

import com.ureca.filmeet.domain.auth.dto.CustomUser;
import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import com.ureca.filmeet.global.util.jwt.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UserQueryService userQueryService;
    private final String defaultRedirectUrl;
    private final String firstLoginRedirectUrl;

    public OAuth2AuthenticationSuccessHandler(TokenService tokenService,
                                              UserQueryService userQueryService,
                                              @Value("${front.redirect-url.default}") String defaultRedirectUrl,
                                              @Value("${front.redirect-url.first-login}") String firstLoginRedirectUrl) {
        this.tokenService = tokenService;
        this.userQueryService = userQueryService;
        this.defaultRedirectUrl = defaultRedirectUrl;
        this.firstLoginRedirectUrl = firstLoginRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        // 사용자 조회 또는 생성
        User user = userQueryService.findByUsername(customUser.getProviderId());

        // JWT 발급
        TokenResponse tokens = tokenService.generateTokens(user.getUsername(), user.getRole());

        // URL에 토큰 포함
        String redirectUrl = user.isFirstLogin() ? firstLoginRedirectUrl : defaultRedirectUrl;
        redirectUrl = String.format("%s?accessToken=%s&refreshToken=%s",
                redirectUrl,
                tokens.accessToken(),
                tokens.refreshToken());

        // 프론트엔드로 리다이렉트
        response.sendRedirect(redirectUrl);

        clearAuthenticationAttributes(request);
    }
}
