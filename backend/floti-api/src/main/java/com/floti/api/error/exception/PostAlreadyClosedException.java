package com.floti.api.error.exception;

public class PostAlreadyClosedException extends RuntimeException {
    public PostAlreadyClosedException() {
        super("채택된 게시글에는 답변할 수 없습니다.");
    }
}
