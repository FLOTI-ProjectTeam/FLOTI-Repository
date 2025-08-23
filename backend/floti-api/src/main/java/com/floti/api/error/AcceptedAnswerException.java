package com.floti.api.error;

public class AcceptedAnswerException extends RuntimeException {
    public AcceptedAnswerException() {
        super("채택된 답변은 삭제할 수 없습니다.");
    }
}
