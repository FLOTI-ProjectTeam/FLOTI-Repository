package com.floti.api.error;

public class AcceptedAnswerUpdateException extends RuntimeException {
    public AcceptedAnswerUpdateException() {
        super("채택된 답변은 수정할 수 없습니다.");
    }
}
