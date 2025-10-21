package com.floti.api.domain.board.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // 테스트용
@AllArgsConstructor
public class ErrorMessage {
    private String message;
    private int code;
}
