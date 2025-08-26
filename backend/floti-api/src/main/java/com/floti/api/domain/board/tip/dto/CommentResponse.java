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
    private AuthorResponse author;
    private String content;
    private Integer likeCount;
    private final boolean deleted; //상위 댓글 전용
    private final boolean liked;
    private final List<CommentResponse> replies; //상위 댓글 전용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;

    public CommentResponse(Comments comment) {
        this(comment, false);
    }

    public CommentResponse(Comments comment, boolean liked) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.parentId = comment.getParentId();
        this.deleted = comment.isDeleted();
        this.replies = new ArrayList<>();
        this.liked = liked;

        if (!this.deleted) {
            this.author = new AuthorResponse(comment.getAuthor());
            this.content = comment.getContent();
            this.likeCount = comment.getLikeCount();
            this.createdAt = comment.getCreatedAt();
        }
    }
}
