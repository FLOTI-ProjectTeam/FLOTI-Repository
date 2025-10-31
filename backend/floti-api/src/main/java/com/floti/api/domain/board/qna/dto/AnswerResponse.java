package com.floti.api.domain.board.qna.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter //테스트용
public class AnswerResponse {
    private final Long id;
    private final Long postId;
    private final AuthorResponse author;
    private final String content;
    private final int likeCount;
    private final boolean accepted;
    private final boolean liked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    public AnswerResponse(Answers answer) {
        this(answer, false);
    }

    public AnswerResponse(Answers answer, boolean liked) {
        this.id = answer.getId();
        this.postId = answer.getPostId();
        this.author = new AuthorResponse(answer.getAuthor());
        this.content = answer.getContent();
        this.likeCount = answer.getLikeCount();
        this.accepted = answer.isAccepted();
        this.createdAt = answer.getCreatedAt();
        this.liked = liked;
    }
}
