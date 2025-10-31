package com.floti.api.domain.board.discussion.dto;

import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import lombok.Getter;

import java.util.List;

@Getter //테스트용
public class DiscussionRoomDetailResponse extends DiscussionRoomResponse {
    private final List<AuthorResponse> participants;
    private final List<MessageResponse> messages;

    public DiscussionRoomDetailResponse(DiscussionRooms discussionRoom, List<AuthorResponse> participants, List<MessageResponse> messages) {
        super(discussionRoom);
        this.participants = participants;
        this.messages = messages;
    }
}
