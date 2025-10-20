package com.floti.api.domain.board.discussion.entity;

import com.floti.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostParticipantId.class)
public class DiscussionParticipants {
    @Id private Long postId;
    @Id private Long participantId;

    @ManyToOne
    @MapsId("postId")
    private DiscussionPosts post;

    @ManyToOne
    @MapsId("participantId")
    private User participant;

    public DiscussionParticipants(Long postId, Long participantId) {
        this.postId = postId;
        this.participantId = participantId;
    }
}
