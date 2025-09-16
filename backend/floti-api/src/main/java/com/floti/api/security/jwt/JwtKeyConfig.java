package com.floti.api.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

/**
 * JwtKeyConfig
 * - JWT 서명용 SecretKey를 관리하는 설정 클래스
 * - 환경변수/설정파일에서 Base64 문자열을 주입받아 SecretKey로 변환한다.
 */

@Configuration
public class JwtKeyConfig {

    // application.yml 에서 jwt.secret-base64 값을 읽어옴
    // 값은 Base64로 인코딩된 문자열이어야 함 (길이 32바이트 이상)
    @Value("${jwt.secret-base64}")
    private String base64Secret;

    /**
     * JWT 서명용 SecretKey Bean 등록
     * - Keys.hmacShaKeyFor: HS256/HS512 같은 HMAC 알고리즘용 키 생성
     * - Base64 디코딩해서 byte[] 배열을 만들고, 그걸로 SecretKey 생성
     */
    @Bean
    public SecretKey jwtSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes); // 최소 32바이트 이상 아니면 예외 발생
    }

}
