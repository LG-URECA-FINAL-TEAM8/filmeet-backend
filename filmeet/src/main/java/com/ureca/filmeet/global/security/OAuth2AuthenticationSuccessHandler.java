package com.ureca.filmeet.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.ResponseCode;
import com.ureca.filmeet.global.util.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 사용자 조회 또는 생성
        String username = oAuth2User.getAttribute("sub");
        Role role = Role.ROLE_USER;

        // JWT 발급
        Map<String, String> tokens = jwtTokenProvider.generateTokens(username, role);

        // JSON 응답 구성
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                ResponseCode.SUCCESS,
                Map.of(
                        "accessToken", tokens.get("accessToken"),
                        "refreshToken", tokens.get("refreshToken")
                )
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        clearAuthenticationAttributes(request);
    }
}
