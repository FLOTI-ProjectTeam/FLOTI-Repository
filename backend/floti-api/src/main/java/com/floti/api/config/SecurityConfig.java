package com.floti.api.config;

import com.floti.api.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // 비밀번호 암호화기 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager (로그인 시 인증 처리에 사용)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 핵심: 보안 필터 체인 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // REST API에서는 CSRF 필요 없음
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 X
                .formLogin(form -> form.disable()) // 폼 로그인 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 공개 허용 경로
                        .requestMatchers("/auth/**").permitAll()
                        // 커뮤니티(챌린지 등) 조회는 로그인 없이 가능하도록 허용
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/community/**").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated())
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // CORS 설정을 위한 Bean 등록 (프론트엔드 연동 필수)
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        config.addAllowedOriginPattern("*"); // 모든 출처 허용 (개발용)
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE 등 모든 메서드 허용
        config.setAllowCredentials(true); // 쿠키/인증정보 포함 허용

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
