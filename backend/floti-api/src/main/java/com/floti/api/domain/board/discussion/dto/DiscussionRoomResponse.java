package com.floti.api.domain.board.discussion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class DiscussionRoomResponse {
    private final Long id;
    private final AuthorResponse author;
    private final String title;
    private final String content;
    private final int maxParticipantCount;
    private final int participantCount;
    private final boolean joined;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime recentActivityAt;

    public DiscussionRoomResponse(DiscussionRooms discussionRoom) {
        this(discussionRoom, false);
    }

    public DiscussionRoomResponse(DiscussionRooms discussionRoom, boolean joined) {
        this.id = discussionRoom.getId();
        this.author = new AuthorResponse(discussionRoom.getAuthor());
        this.title = discussionRoom.getTitle();
        this.content = discussionRoom.getContent();
        this.maxParticipantCount = discussionRoom.getMaxParticipantCount();
        this.participantCount = discussionRoom.getParticipantCount();
        this.joined = joined;
        this.recentActivityAt = discussionRoom.getRecentActivityAt();
    }
}
