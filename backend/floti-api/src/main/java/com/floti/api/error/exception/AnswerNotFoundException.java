package com.floti.api.error.exception;

public class AnswerNotFoundException extends RuntimeException {
    public AnswerNotFoundException() {
        super("답변을 찾을 수 없습니다.");
    }
}
