package com.floti.api.domain.board.tip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public abstract class BaseCommentResponse {
    protected final Long id;
    protected final Long postId;
    protected final AuthorResponse author;
    protected String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    protected LocalDateTime createdAt;

    protected BaseCommentResponse(Comments comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.author = new AuthorResponse(comment.getAuthor());
    }
}
