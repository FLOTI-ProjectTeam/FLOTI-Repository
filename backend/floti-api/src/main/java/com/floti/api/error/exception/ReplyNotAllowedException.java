package com.floti.api.error.exception;

public class ReplyNotAllowedException extends RuntimeException {
    public ReplyNotAllowedException() {
        super("답글은 최상위 댓글에만 작성할 수 있습니다.");
    }
}
