package com.floti.api.error;

public class ReplyDepthExceededException extends RuntimeException {
    public ReplyDepthExceededException() {
        super("답글은 최상위 댓글에만 가능합니다.");
    }
}
