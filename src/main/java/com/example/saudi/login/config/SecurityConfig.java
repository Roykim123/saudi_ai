package com.example.saudi.login.config;
import com.example.saudi.jwt.JwtAuthenticationFilter;
import com.example.saudi.jwt.JwtUtil;
import com.example.saudi.login.service.OAuth2UserService;
import com.example.saudi.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    public SecurityConfig(OAuth2UserService oAuth2UserService, UserService userService, JwtUtil jwtUtil) {
        this.oAuth2UserService = oAuth2UserService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());

        http.authorizeHttpRequests(config -> config
                .requestMatchers("/main", "/posts/**", "/mypage/**").authenticated()
                .anyRequest().permitAll());

        http.oauth2Login(oauth2Configurer -> oauth2Configurer
                .loginPage("/login")
                .successHandler(successHandler(jwtUtil))
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService)));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 성공 시 받아올 값을 지정하는 핸들러 - 이메일 필요하면 추가
    @Bean
    public AuthenticationSuccessHandler successHandler(JwtUtil jwtUtil) {
        return ((request, response, authentication) -> {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = defaultOAuth2User.getAttributes();
            String id = defaultOAuth2User.getAttributes().get("id").toString();

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<String, Object> profile = (Map<String, Object>) attributes.get("properties");
            String nickname = profile != null ? (String) profile.get("nickname") : null;
            String profileImage = profile != null ? (String) profile.get("profile_image") : null;

            // 사용자 정보 DB 저장
            userService.saveUser(id, email, nickname, profileImage);

            // JWT 발급
            String token = jwtUtil.generateToken(id);

            // 응답으로 JSON 반환 (리디렉션 대신 JSON 응답)
            String body = """
                    {
                        "token": "%s",
                        "email": "%s"
                    }
                    """.formatted(token, email);

            // 클라이언트 헤더로 JWT 내려줌 -> 프론트가 쿠키/로컬스토리지에 저장 가능
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Authorization", "Bearer " + token);

            PrintWriter writer = response.getWriter();
            writer.println(body);
            writer.flush();
        });
    }
}