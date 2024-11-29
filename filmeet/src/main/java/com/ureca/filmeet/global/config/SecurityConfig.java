package com.ureca.filmeet.global.config;

import com.ureca.filmeet.domain.auth.service.CustomOAuth2UserService;
import com.ureca.filmeet.domain.auth.service.CustomOidcUserService;
import com.ureca.filmeet.global.filter.JwtAuthenticationFilter;
import com.ureca.filmeet.global.security.CustomAccessDeniedHandler;
import com.ureca.filmeet.global.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ureca.filmeet.global.security.JwtAuthenticationEntryPoint;
import com.ureca.filmeet.global.security.OAuth2AuthenticationSuccessHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService) // OIDC Flow (Google)
                                .userService(customOAuth2UserService)  // OAuth2 Flow (Naver)
                        )
                        .successHandler((request, response, authentication) ->
                                oAuth2AuthenticationSuccessHandler
                                        .onAuthenticationSuccess(request, response, authentication))
                )
                .addFilterAfter(jwtAuthenticationFilter, ExceptionTranslationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health").permitAll() // 먼저 선언
                        .requestMatchers("/**", "/images/**", "/users/signup", "/auth/**", "/swagger",
                                "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // React 앱의 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 자격 증명 허용 (예: 쿠키)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
