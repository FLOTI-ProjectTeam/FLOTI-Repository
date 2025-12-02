package com.floti.api.error;

public class ExceptionMessage {
    public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    public static final String POST_NOT_FOUND = "게시글을 찾을 수 없습니다.";
    public static final String COMMENT_NOT_FOUND = "댓글을 찾을 수 없습니다.";
    public static final String ANSWER_NOT_FOUND = "답변을 찾을 수 없습니다.";
    public static final String UPDATE_DENIED = "수정할 권한이 없습니다.";
    public static final String DELETE_DENIED = "삭제할 권한이 없습니다.";

    /*
     * 챌린지 게시판 관련 오류 메시지
     * 기존 게시판 오류 메시지에 더하여 챌린지에서 자주 발생할 수 있는 예외 상황을 정의한다.
     */
    public static final String CHALLENGE_NOT_FOUND = "챌린지를 찾을 수 없습니다.";
    public static final String FEED_NOT_FOUND = "피드를 찾을 수 없습니다.";
    public static final String JOIN_DENIED = "참여할 수 없습니다.";
    public static final String CHALLENGE_FULL = "참여 가능한 인원을 초과하였습니다.";
    public static final String NOT_PARTICIPANT = "참여자가 아닙니다.";

}
