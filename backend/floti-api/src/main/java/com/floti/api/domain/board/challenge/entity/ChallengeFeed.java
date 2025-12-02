package com.floti.api.domain.board.challenge.entity;

import com.floti.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 챌린지 성과 공유(피드) 엔티티.
 * 특정 챌린지에 대한 참가자의 글을 저장하며 사용자와 챌린지 사이의 관계를 갖는다.
 */
@Entity
@Getter
@Table(name = "feeds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeFeed {
    /**
     * 피드 ID (기본 키).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    /**
     * 어떤 챌린지에 대한 글인지.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengePosts challenge;

    /**
     * 글 작성자.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 피드 내용. 최대 255자.
     */
    @Column(nullable = false, length = 255)
    private String content;

    /**
     * 작성 시간. @PrePersist로 자동 채워진다.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 빌더로 엔티티를 생성한다. createdAt은 persist 시점에 자동 세팅된다.
     */
    @Builder
    public ChallengeFeed(ChallengePosts challenge, User author, String content) {
        this.challenge = challenge;
        this.author = author;
        this.content = content;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * 피드 내용을 수정한다.
     */
    public void updateContent(String content) {
        this.content = content;
    }
}