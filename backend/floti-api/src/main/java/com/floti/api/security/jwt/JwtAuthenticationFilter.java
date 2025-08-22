package com.floti.api.security.jwt;

// 스프링/서블릿 관련 import
import jakarta.servlet.FilterChain;                       // 필터 체인
import jakarta.servlet.ServletException;                  // 서블릿 예외
import jakarta.servlet.http.HttpServletRequest;           // 요청 객체
import jakarta.servlet.http.HttpServletResponse;          // 응답 객체
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 인증 토큰
import org.springframework.security.core.context.SecurityContextHolder; // 시큐리티 컨텍스트
import org.springframework.security.core.userdetails.User; // 스프링 시큐리티 User(간단 구현)
import org.springframework.security.core.userdetails.UserDetails; // 사용자 정보 인터페이스
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // 세부정보
import org.springframework.stereotype.Component;           // 스프링 빈
import org.springframework.web.filter.OncePerRequestFilter; // 요청당 1회 실행 보장 필터
import java.io.IOException;                               // IO 예외
import java.util.Collections;                             // 빈 권한 컬렉션

// JWT를 읽어 인증객체를 세팅하는 커스텀 필터
@Component // 스프링이 자동 등록
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter: 요청 한 번에 딱 1회만 실행되도록 보장하는 기반 클래스(

    private final JwtUtil jwtUtil; // JWT 유틸 주입

    // 생성자 주입
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출("Bearer x.y.z" 형태 기대)
        String authHeader = request.getHeader("Authorization"); // 헤더 읽기

        // 토큰 추출 변수
        String token = null; // 실제 JWT 문자열(헤더에서 파싱)
        String username = null; // 토큰에서 꺼낸 username

        // "Bearer "로 시작하면 토큰 부분만 잘라냄
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " 이후 문자열
            // 토큰이 유효하면 username 추출
            if (jwtUtil.isValid(token)) {
                username = jwtUtil.extractUsername(token); // subject=username
            }
        }

        // 아직 인증이 안 되어 있고(username 존재) 유효한 경우 인증객체 세팅
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 간단히 ROLE_USER 하나만 가진 사용자로 구성(실무는 권한 테이블 등에서 로드)
            UserDetails userDetails = User
                    .withUsername(username)                // 사용자명
                    .password("N/A")                       // 비번은 사용 안 함
                    .authorities(Collections.singleton(() -> "ROLE_USER")) // 권한
                    .build();

            // 인증 토큰 생성(비밀번호 검증은 이미 로그인 시 끝)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,                   // 주체(사용자)
                            null,                          // 자격증명(여기선 불필요)
                            userDetails.getAuthorities()   // 권한 목록
                    );

            // 요청 메타정보 세팅(IP 등)
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 시큐리티 컨텍스트에 인증 결과 저장 → 이후 컨트롤러에서 인증된 사용자로 인식
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);

    }
}
