package com.floti.api.error.exception;

public class AcceptedAnswerDeletionException extends RuntimeException {
    public AcceptedAnswerDeletionException() {
        super("채택된 답변은 삭제할 수 없습니다.");
    }
}
