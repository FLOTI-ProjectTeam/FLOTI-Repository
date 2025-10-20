package com.floti.api.domain.board.discussion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class DiscussionPostResponse {
    private final Long id;
    private final AuthorResponse author;
    private final String title;
    private final String intro;
    private final int maxParticipants;
    private final int participantCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;

    public DiscussionPostResponse(DiscussionPosts discussionPost) {
        this.id = discussionPost.getId();
        this.author = new AuthorResponse(discussionPost.getAuthor());
        this.title = discussionPost.getTitle();
        this.intro = discussionPost.getIntro();
        this.maxParticipants = discussionPost.getMaxParticipants();
        this.participantCount = discussionPost.getParticipantCount();
        this.createdAt = discussionPost.getCreatedAt();
    }
}
