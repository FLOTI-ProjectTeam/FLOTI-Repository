package com.floti.api.domain.board.discussion.entity;

import com.floti.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscussionPosts {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String intro;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private int participantCount = 1;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public DiscussionPosts(User author, String title, String intro, int maxParticipants) {
        this.author = author;
        this.title = title;
        this.intro = intro;
        this.maxParticipants = maxParticipants;
    }

    public void update(String title, String intro, int maxParticipants) {
        this.title = title;
        this.intro = intro;
        this.maxParticipants = maxParticipants;
    }

    public void incrementParticipantCount() {
        this.participantCount++;
    }

    public void decrementParticipantCount() {
        if (this.participantCount > 1) this.participantCount--;
    }
}
