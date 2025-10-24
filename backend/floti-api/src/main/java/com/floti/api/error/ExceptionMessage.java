package com.floti.api.error;

public class ExceptionMessage {
    /* 403 Forbidden */
    public static final String UPDATE_DENIED = "수정할 권한이 없습니다.";
    public static final String DELETE_DENIED = "삭제할 권한이 없습니다.";
    public static final String HOST_CANNOT_LEAVE = "주최자는 나갈 수 없습니다.";

    /* 404 Not Found */
    public static final String POST_NOT_FOUND = "게시글을 찾을 수 없습니다.";
    public static final String COMMENT_NOT_FOUND = "댓글을 찾을 수 없습니다.";
    public static final String ANSWER_NOT_FOUND = "답변을 찾을 수 없습니다.";
    public static final String MESSAGE_NOT_FOUND = "메시지를 찾을 수 없습니다.";
}
