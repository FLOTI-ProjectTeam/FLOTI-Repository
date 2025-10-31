package com.floti.api.domain.board.discussion.entity;

import com.floti.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RoomParticipantId.class)
public class DiscussionParticipants {
    @Id private Long roomId;
    @Id private Long participantId;

    @ManyToOne
    @MapsId("roomId") // roomId 필드에 엔티티의 id 값을 자동 매핑
    private DiscussionRooms room;

    @ManyToOne
    @MapsId("participantId") // participantId 필드에 엔티티의 id 값을 자동 매핑
    private User participant;

    public DiscussionParticipants(DiscussionRooms room, User participant) {
        this.room = room;
        this.participant = participant;
    }
}
