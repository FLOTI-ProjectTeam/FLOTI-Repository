package com.floti.api.error.exception;

public class MaxParticipantExceededException extends RuntimeException {
    public MaxParticipantExceededException() {
        super("최대 참여 인원을 초과했습니다.");
    }
}
