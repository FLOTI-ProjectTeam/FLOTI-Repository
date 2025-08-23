package com.floti.api.error;

public class AcceptedPostException extends RuntimeException {
    public AcceptedPostException() {
        super("이미 채택된 게시글입니다.");
    }
}
