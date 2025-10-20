package com.floti.api.error.exception;

public class ImageDeleteFailedException extends RuntimeException {
    public ImageDeleteFailedException() {
        super("이미지 삭제에 실패하였습니다.");
    }
}
