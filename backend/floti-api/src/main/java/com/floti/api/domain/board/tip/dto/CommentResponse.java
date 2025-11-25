package com.floti.api.domain.board.tip.dto;

import com.floti.api.domain.board.tip.entity.Comments;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter //테스트용
public class CommentResponse extends BaseCommentResponse {
    private final boolean deleted;
    private final List<ReplyResponse> replies = new ArrayList<>();

    public CommentResponse(Comments comment) {
        super(comment);
        this.deleted = comment.isDeleted();

        if (!this.deleted) {
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
        }
    }

    public void addReply(ReplyResponse response) {
        this.replies.add(response);
    }
}
