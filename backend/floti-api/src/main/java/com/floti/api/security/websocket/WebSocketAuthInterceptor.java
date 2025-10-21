package com.floti.api.security.websocket;

import com.floti.api.security.jwt.JwtUtil;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;

    /* STOMP 메시지가 채널로 전송되기 전에 호출 */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 1. STOMP 헤더를 편리하게 접근 가능한 래퍼 객체 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 2. 현재 메시지가 CONNECT 명령인지 확인
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization"); // JWT 토근 로드

            // 2-2. 토큰이 존재하면 인증 처리 수행
            if (token != null) {
                Authentication auth = parseTokenToAuthentication(token); // JWT 토큰 → Authentication 객체
                accessor.setUser(auth); // 인증 정보를 STOMP 세션에 설정
            }
        }

        // 3. 다음 채널 단계로 메시지 전달
        return message;
    }

    /* JWT 토큰을 Authentication 객체로 변환 */
    private Authentication parseTokenToAuthentication(String token) {
        // 1. JWT 검증
        if (!jwtUtil.isValid(token))
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");

        // 2. JWT에서 사용자 ID 추출
        String username = jwtUtil.extractUsername(token);

        // 3. 사용자 정보를 UserDetails로 로드
        UserDetails userDetails = authUtil.loadUserByUsername(username);

        // 4. Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }
}
