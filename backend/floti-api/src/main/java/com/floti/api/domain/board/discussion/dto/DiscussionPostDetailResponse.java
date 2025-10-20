package com.floti.api.domain.board.discussion.dto;

import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import lombok.Getter;

import java.util.List;

@Getter //테스트용
public class DiscussionPostDetailResponse extends DiscussionPostResponse {
    private final List<AuthorResponse> participants;
    private final List<MessageResponse> messages;

    public DiscussionPostDetailResponse(DiscussionPosts discussionPost, List<AuthorResponse> participants, List<MessageResponse> messages) {
        super(discussionPost);
        this.participants = participants;
        this.messages = messages;
    }
}
