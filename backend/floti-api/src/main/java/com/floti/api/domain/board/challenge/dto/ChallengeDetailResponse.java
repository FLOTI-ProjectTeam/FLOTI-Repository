package com.floti.api.domain.board.challenge.dto;

import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import com.floti.api.domain.board.common.dto.AuthorResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 챌린지 상세 조회 시 사용되는 응답 DTO.
 * record 적용 : 불변 데이터 전달 목적에 적합
 */
public record ChallengeDetailResponse(
        Long id,
        String title,
        String intro,
        String content,
        AuthorResponse author,
        int maxParticipants,
        int currentParticipants,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isCompleted,
        LocalDateTime createdAt,
        List<ParticipantResponse> participants,
        double progress,
        Integer myProgress
) {
    public ChallengeDetailResponse(ChallengePosts post,
                                   List<ParticipantResponse> participants,
                                   double progress,
                                   Integer myProgress) {
        this(
                post.getId(),
                post.getTitle(),
                post.getIntro(),
                post.getContent(),
                new AuthorResponse(post.getAuthor()),
                post.getMaxParticipants(),
                post.getCurrentParticipants(),
                post.getStartDate(),
                post.getEndDate(),
                post.isCompleted(),
                post.getCreatedAt(),
                participants,
                progress,
                myProgress
        );
    }
}
