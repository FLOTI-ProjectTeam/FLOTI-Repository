package com.floti.api.error;

public class DeletedCommentException extends RuntimeException {
    public DeletedCommentException() {
        super("삭제된 댓글입니다.");
    }
}
