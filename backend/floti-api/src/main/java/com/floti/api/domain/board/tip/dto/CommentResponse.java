package com.floti.api.domain.board.tip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter //테스트용
public class CommentResponse {
    private final Long id;
    private final Long postId;
    private final Long parentId; //하위 댓글 전용
    private final AuthorResponse author;
    private String content;
    private final boolean deleted; //상위 댓글 전용
    private final List<CommentResponse> replies; //상위 댓글 전용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDateTime createdAt;

    public CommentResponse(Comments comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.parentId = comment.getParentId();
        this.author = new AuthorResponse(comment.getAuthor());
        this.deleted = comment.isDeleted();
        this.replies = new ArrayList<>();

        if (!this.deleted) {
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
        }
    }
}
