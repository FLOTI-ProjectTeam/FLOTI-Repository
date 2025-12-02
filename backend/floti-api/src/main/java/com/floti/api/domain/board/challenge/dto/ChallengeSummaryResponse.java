package com.floti.api.domain.board.challenge.dto;

import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 챌린지 목록 조회 시 사용되는 응답 DTO.
 * 상세한 설명 대신 요약 정보를 제공하여 목록 출력에 적합하다.
 */
@Getter
public class ChallengeSummaryResponse {
    private final Long id;
    private final String title;
    private final String intro;
    private final AuthorResponse author;
    private final int maxParticipants;
    private final int currentParticipants;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean isCompleted;
    private final LocalDateTime createdAt;

    public ChallengeSummaryResponse(ChallengePosts post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.intro = post.getIntro();
        this.author = new AuthorResponse(post.getAuthor());
        this.maxParticipants = post.getMaxParticipants();
        this.currentParticipants = post.getCurrentParticipants();
        this.startDate = post.getStartDate();
        this.endDate = post.getEndDate();
        this.isCompleted = post.isCompleted();
        this.createdAt = post.getCreatedAt();
    }
}