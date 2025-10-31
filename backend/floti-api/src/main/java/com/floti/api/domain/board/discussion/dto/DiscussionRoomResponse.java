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
    private final String intro;
    private final int maxParticipants;
    private final int participantCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime recentActivityAt;

    public DiscussionRoomResponse(DiscussionRooms discussionRoom) {
        this.id = discussionRoom.getId();
        this.author = new AuthorResponse(discussionRoom.getAuthor());
        this.title = discussionRoom.getTitle();
        this.intro = discussionRoom.getIntro();
        this.maxParticipants = discussionRoom.getMaxParticipants();
        this.participantCount = discussionRoom.getParticipantCount();
        this.recentActivityAt = discussionRoom.getRecentActivityAt();
    }
}
