package com.floti.api.domain.board.qna.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class AnswerResponse {
    private final Long id;
    private final Long postId;
    private final AuthorResponse author;
    private final String content;
    private final Integer likeCount;
    private final boolean accepted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;

    public AnswerResponse(Answers answers) {
        this.id = answers.getId();
        this.postId = answers.getPostId();
        this.author = new AuthorResponse(answers.getAuthor());
        this.content = answers.getContent();
        this.likeCount = answers.getLikeCount();
        this.accepted = answers.isAccepted();
        this.createdAt = answers.getCreatedAt();
    }
}
