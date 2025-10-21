package com.floti.api.error.exception;

public class MessageDeleteDeniedException extends RuntimeException {
    public MessageDeleteDeniedException() {
        super("5분 이내 전송된 메시지만 삭제할 수 있습니다.");
    }
}
