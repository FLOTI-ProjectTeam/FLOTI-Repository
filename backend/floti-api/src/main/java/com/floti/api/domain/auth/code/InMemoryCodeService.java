package com.floti.api.domain.auth.code;

import org.springframework.stereotype.Service;            // 스프링 서비스
import java.time.Instant;                                 // 만료 시간 계산
import java.util.Map;                                     // 맵 자료구조
import java.util.Random;                                  // 난수 코드 생성
import java.util.concurrent.ConcurrentHashMap;            // 스레드 안전 맵

// 실제 구현: 메모리 기반 코드 저장(추후 Redis로 쉽게 교체 가능)
@Service // 스프링 빈으로 사용
public class InMemoryCodeService {

    // 코드 보관 맵: key = CodeType+email (간단 키)
    private final Map<String, CodeEntry> store = new ConcurrentHashMap<>();

    // 코드 유효 시간(분) - MVP로 5분 고정
    private static final long TTL_MINUTES = 5L;

    // 코드 생성기(숫자+문자 8자리 예시)
    private String randomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    // 키 만들기(타입+이메일)
    private String key(CodeType type, String email) {
        return type.name() + ":" + email;
    }

    // 코드 발급(“전송”은 실제로 메일 전송 API 연동 시 수행; 여기선 코드 반환/로그 처리)
    public String issue(CodeType type, String email) {
        String code = randomCode(); // 새 코드 생성
        Instant exp = Instant.now().plusSeconds(TTL_MINUTES * 60); // 만료 시각(지금+5분)
        store.put(key(type, email), new CodeEntry(email, code, exp)); // 저장
        return code; // 코드 반환(이메일 전송 대상)
    }

    // 코드 검증
    public boolean verify(CodeType type, String email, String code) {
        CodeEntry entry = store.get(key(type, email)); // 저장된 값 꺼내기
        if (entry == null) return false; // 없으면 실패
        if (Instant.now().isAfter(entry.expiresAt)) return false; // 만료됐으면 실패
        return entry.code.equals(code); // 값이 같으면 성공
    }

    // 사용 완료 시 제거(선택)
    public void consume(CodeType type, String email) {
        store.remove(key(type, email)); // 맵에서 제거
    }

}


