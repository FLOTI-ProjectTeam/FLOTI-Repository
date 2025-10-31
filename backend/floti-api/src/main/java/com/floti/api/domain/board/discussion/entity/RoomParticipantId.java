package com.floti.api.domain.board.discussion.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoomParticipantId {
    private Long roomId;
    private Long participantId;
}
