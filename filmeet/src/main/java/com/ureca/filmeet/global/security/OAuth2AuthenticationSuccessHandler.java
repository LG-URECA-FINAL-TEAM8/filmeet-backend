package com.ureca.filmeet.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.filmeet.domain.auth.dto.CustomUser;
import com.ureca.filmeet.domain.auth.dto.response.TokenResponse;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import com.ureca.filmeet.global.util.jwt.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        // 사용자 조회 또는 생성
        String username = customUser.getProviderId();
        Role role = Role.ROLE_USER;

        // JWT 발급
        TokenResponse tokens = tokenService.generateTokens(username, role);

        // JSON 응답 구성
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                ResponseCode.SUCCESS,
                Map.of(
                        "accessToken", tokens.accessToken(),
                        "refreshToken", tokens.refreshToken()
                )
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        clearAuthenticationAttributes(request);
    }
}
