package com.floti.api.error.exception;

public class ImageSaveFailedException extends RuntimeException {
    public ImageSaveFailedException() {
        super("이미지 저장에 실패하였습니다.");
    }
}
