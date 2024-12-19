package com.ureca.filmeet.global.config;

import com.ureca.filmeet.domain.auth.service.CustomOAuth2UserService;
import com.ureca.filmeet.domain.auth.service.CustomOidcUserService;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.global.filter.JwtAuthenticationFilter;
import com.ureca.filmeet.global.security.CustomAccessDeniedHandler;
import com.ureca.filmeet.global.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ureca.filmeet.global.security.JwtAuthenticationEntryPoint;
import com.ureca.filmeet.global.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    SecurityFilterChain securityFilterChain(HttpSecurity http, RoleHierarchy roleHierarchy) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                        .userInfoEndpoint(
                                userInfo -> userInfo.oidcUserService(customOidcUserService) // OIDC Flow (Google)
                                        .userService(customOAuth2UserService))// OAuth2 Flow (Naver)

                        .successHandler(
                                (request, response, authentication) -> oAuth2AuthenticationSuccessHandler.onAuthenticationSuccess(
                                        request, response, authentication)))

                .addFilterAfter(jwtAuthenticationFilter, ExceptionTranslationFilter.class)

                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))

                .authorizeHttpRequests(authorize -> authorize
                        // 기본 허용 경로
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/images/**", "/error", "/users/signup", "/users/check-username",
                                "/auth/login", "/auth/refresh").permitAll()

                        // 리뷰 관련 경로 허용
                        .requestMatchers(HttpMethod.GET, "/reviews/movies/*").permitAll() // 영화 리뷰 목록 조회
                        .requestMatchers(HttpMethod.GET, "/reviews/movies/*").permitAll() // 영화 리뷰 목록 조회
                        .requestMatchers(HttpMethod.GET, "/reviews/*").permitAll()        // 리뷰 상세 조회
                        .requestMatchers(HttpMethod.GET, "/reviews/users")
                        .permitAll() // 지금 뜨는 리뷰 조회 (쿼리 파라미터는 컨트롤러에서 검증)

                        // 영화 관련 경로 허용
                        .requestMatchers(HttpMethod.GET, "/movies/upcoming").permitAll()        // 공개 예정작
                        .requestMatchers(HttpMethod.GET, "/movies/boxoffice").permitAll()       // 박스오피스 순위
                        .requestMatchers(HttpMethod.GET, "/movies/rankings").permitAll()        // TOP 10 영화
                        .requestMatchers(HttpMethod.GET, "/movies/admin-rankings").permitAll()        // 영화 상세 조회
                        .requestMatchers(HttpMethod.GET, "/movies/detail/*").permitAll()        // 영화 상세 조회
                        .requestMatchers(HttpMethod.GET, "/movies/search/genre").permitAll()    // 장르 검색
                        .requestMatchers(HttpMethod.GET, "/movies/search/title").permitAll()    // 제목 검색
                        .requestMatchers(HttpMethod.GET, "/movies/random").permitAll()          // 랜덤 영화 조회
                        .requestMatchers(HttpMethod.GET, "/movies/recommendation/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/movies/total/ratings").permitAll()

                        // 컬렉션 관련 경로 허용
                        .requestMatchers(HttpMethod.GET, "/collections/search/title").permitAll() // 컬렉션 제목 검색
                        .anyRequest().authenticated());

        // 계층 설정이 적용되도록 강제로 추가
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                List.of("http://localhost:5173", "https://prod.d2r305hbtyzes4.amplifyapp.com",
                        "https://filmeet.me")); // 허용할 Origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        // 계층 설정
        String hierarchy = """
                ROLE_SUPER_ADMIN > ROLE_MOVIE_ADMIN
                ROLE_SUPER_ADMIN > ROLE_REVIEW_ADMIN
                ROLE_MOVIE_ADMIN > ROLE_ADULT_USER
                ROLE_REVIEW_ADMIN > ROLE_ADULT_USER
                ROLE_ADULT_USER > ROLE_MINOR_USER
                """;

        roleHierarchy.setHierarchy(hierarchy);

        Role.setRoleHierarchy(roleHierarchy);

        return roleHierarchy;
    }
}