package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정들을 활성화
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig) -> csrfConfig.disable())
                .headers((headerConfig) -> headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())) // h2-console 화면을 사용하기 위해 해당 옵션들을 비활성화
                .authorizeHttpRequests((authorizeRequests) -> // authorizeHttpRequests : URL별 권한 관리를 설정하는 옵션의 시작점
                        authorizeRequests
                                .requestMatchers("/","/css/**", "/images/**","/js/**", "/h2-console/**").permitAll() // requestMatchers : 권한 관리 대상을 지정하는 옵션
                                .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                                .anyRequest().authenticated() // anyRequest : 설정된 값들 이외 나머지 URL들을 나타냄
                )
                .logout((logoutConfig) ->
                        logoutConfig.logoutSuccessUrl("/")) // logoutConfig.logoutSuccessUrl : 로그아웃 기능에 대한 여러 설정의 진입점
                .oauth2Login((oauth2) -> oauth2 // oauth2Login : OAuth2 로그인 기능에 대한 여러 설정의 진입점
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint // userInfoEndpoint : OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                                .userService(customOAuth2UserService))); // userService : 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록

        return http.build();
    }

}
