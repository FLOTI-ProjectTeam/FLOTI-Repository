package com.floti.api.domain.board.challenge.dto;

import com.floti.api.domain.board.challenge.entity.ChallengeFeed;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 챌린지 피드 조회 시 사용되는 응답 DTO.
 */
@Getter
public class FeedResponse {
    private final Long id;
    private final AuthorResponse author;
    private final String content;
    private final LocalDateTime createdAt;

    public FeedResponse(ChallengeFeed feed) {
        this.id = feed.getId();
        this.author = new AuthorResponse(feed.getAuthor());
        this.content = feed.getContent();
        this.createdAt = feed.getCreatedAt();
    }
}