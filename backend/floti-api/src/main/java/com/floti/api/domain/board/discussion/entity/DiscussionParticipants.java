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
    @MapsId("postId") // postId 필드에 엔티티의 id 값을 자동 매핑
    private DiscussionPosts post;

    @ManyToOne
    @MapsId("participantId") // participantId 필드에 엔티티의 id 값을 자동 매핑
    private User participant;

    public DiscussionParticipants(DiscussionPosts post, User participant) {
        this.post = post;
        this.participant = participant;
    }
}
