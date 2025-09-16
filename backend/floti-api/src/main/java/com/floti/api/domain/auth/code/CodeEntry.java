package com.floti.api.domain.auth.code;

import java.time.Instant;

// 코드 정보를 담는 내부 클래스(값 객체)
class CodeEntry {
    final String email;       // 대상 이메일
    final String code;        // 전송된 코드 문자열
    final Instant expiresAt;  // 만료 시각
    CodeEntry(String email, String code, Instant expiresAt) {
        this.email = email; this.code = code; this.expiresAt = expiresAt;
    }
}
