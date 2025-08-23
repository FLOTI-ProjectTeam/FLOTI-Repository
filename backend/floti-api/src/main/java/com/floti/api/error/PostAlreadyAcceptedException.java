package com.floti.api.error;

public class PostAlreadyAcceptedException extends RuntimeException {
    public PostAlreadyAcceptedException() {
        super("이미 채택된 게시글입니다.");
    }
}
