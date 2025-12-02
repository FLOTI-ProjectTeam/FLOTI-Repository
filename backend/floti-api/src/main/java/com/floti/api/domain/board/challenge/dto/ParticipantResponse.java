package com.floti.api.domain.board.challenge.dto;

import com.floti.api.domain.auth.entity.User;
import lombok.Getter;

/**
 * 특정 챌린지 참가자 정보를 전달하기 위한 DTO.
 * 사용자 기본 정보와 진행률, 공헌도를 포함한다.
 */
@Getter
public class ParticipantResponse {
    private final Long id;
    private final String nickname;
    private final String profileImage;
    private final int progress;
    private final int contribution;

    public ParticipantResponse(User user, int progress, int contribution) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.progress = progress;
        this.contribution = contribution;
    }
}