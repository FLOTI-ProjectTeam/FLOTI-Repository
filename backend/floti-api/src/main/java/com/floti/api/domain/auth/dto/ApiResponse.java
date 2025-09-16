package com.floti.api.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 공통 응답 래퍼(성공/데이터/메시지)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;          // 성공 여부
    private T data;                   // 결과 데이터(없으면 null)
    private String message;           // 설명 메시지(없으면 null)

    // 성공 헬퍼
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    // 실패 헬퍼
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}