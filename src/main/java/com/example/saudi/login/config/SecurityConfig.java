package com.example.saudi.login.config;
import com.example.saudi.login.service.OAuth2UserService;
import com.example.saudi.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final UserService userService;
    public SecurityConfig(OAuth2UserService oAuth2UserService, UserService userService) {
        this.oAuth2UserService = oAuth2UserService;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) ->
                        csrf.disable()
                );
        http.authorizeHttpRequests(config -> config.anyRequest().permitAll());
        http.oauth2Login(oauth2Configurer -> oauth2Configurer
                .loginPage("/login")
                .successHandler(successHandler())
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserService)));
        return http.build();
    }

    // 성공 시 받아올 값을 지정하는 핸들러 - 이메일 필요하면 추가
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return ((request, response, authentication) -> {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            Map<String, Object> attributes = defaultOAuth2User.getAttributes();
            String id = defaultOAuth2User.getAttributes().get("id").toString();

            // kakao_account 내부에서 email 추출
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            // 사용자 정보 db에 저장
            userService.saveUser(id, email);

            String body = """
                    {"id":"%s",
                     "email": "%s"
                     }
                    """.formatted(id, email);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            PrintWriter writer = response.getWriter();
            writer.println(body);
            writer.flush();

            // 로그인 성공시 메인페이지 이동
            //response.sendRedirect("/login/main");
        });
    }
}