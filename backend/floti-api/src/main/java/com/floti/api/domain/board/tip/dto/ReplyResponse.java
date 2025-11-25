package com.floti.api.domain.board.tip.dto;

import com.floti.api.domain.board.tip.entity.Comments;
import lombok.Getter;

@Getter //테스트용
public class ReplyResponse extends BaseCommentResponse {
    private final Long parentId;

    public ReplyResponse(Comments comment) {
        super(comment);
        this.parentId = comment.getParentId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}
