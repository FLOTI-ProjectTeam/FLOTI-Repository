package com.floti.api.domain.board.discussion.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostParticipantId {
    private Long postId;
    private Long participantId;
}
