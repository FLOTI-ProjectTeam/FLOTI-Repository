package com.floti.api.security.jwt; // 패키지: 보안 관련 유틸을 모아둘 위치

// 아래 import는 JWT 생성/검증에 필요한 JJWT 클래스들
import io.jsonwebtoken.Claims;              // JWT 내부 클레임(데이터) 파싱용
import io.jsonwebtoken.Jwts;                // JWT 빌더/파서 진입점
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;       // 키 생성/관리 유틸
import javax.crypto.SecretKey;              // 대칭키 타입

import org.springframework.beans.factory.annotation.Value; // application.yml 값 주입
import org.springframework.stereotype.Component;            // 스프링 빈 등록
import java.util.Date;                      // 만료시간 계산
import java.time.Instant;                   // 현재 시간 취득
import java.time.temporal.ChronoUnit;       // 시간 더하기(만료 계산)


// JWT 생성/검증을 담당하는 유틸 컴포넌트
@Component // 스프링 빈으로 등록하여 주입 가능하게 함
public class JwtUtil {

    // application.yml에서 비밀키 문자열을 주입받음 (환경변수로 세팅 권장)
    @Value("${jwt.secret}")
    private String secret; // 실제 서명에 사용할 비밀(절대 코드에 하드코딩 금지)

    // 토큰 만료(분) - yml에서 주입받고, 없으면 기본 60분
    @Value("${jwt.exp-minutes:60}")
    private long expMinutes; // 액세스 토큰 만료 시간(분 단위)

    // 서명용 SecretKey를 생성(매 요청마다 문자열→키 변환)
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // JWT 생성: 주체(subject)=username, 만료시간 포함
    public String generateToken(String username) {
        // 만료 시각 계산(현재 + expMinutes)
        Instant now = Instant.now();
        Instant exp = now.plus(expMinutes, ChronoUnit.MINUTES);

        // HS256 알고리즘으로 서명된 JWT를 생성
        return Jwts.builder()
                .setSubject(username) // 토큰의 주체
                .setIssuedAt(Date.from(now)) // 발급 시각
                .setExpiration(Date.from(exp)) // 만료 시각
                .signWith(getKey(), SignatureAlgorithm.HS256) // 서명(HS256 + 비밀키)
                .compact(); // 문자열 토큰 생성
    }

    // 토큰에서 username(subject) 추출
    public String extractUsername(String token) {
        return getAllClaims(token).getSubject(); // subject가 바로 username
    }

    // 토큰 유효성 검사(서명/만료)
    public boolean isValid(String token) {
        try {
            // 파싱 과정에서 유효하지 않으면 예외 발생 -> false 반환
            getAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 내부 유틸: 모든 클레임 파싱(서명 검증 포함)
    private Claims getAllClaims(String token) {
        return Jwts.parser()             // 파서 빌더로 시작
                .setSigningKey(getKey())        // 서명 검증 키 설정
                .build()                        // 파서 생성
                .parseClaimsJws(token)          // JWS(서명된 JWT) 파싱
                .getBody();                     // 클레임(내용) 반환
    }

}
