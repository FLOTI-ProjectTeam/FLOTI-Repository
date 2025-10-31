package com.floti.api.domain.board.discussion.entity;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.entity.LikeableEntity;
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
public class Messages implements LikeableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    private int likeCount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Messages(Long id, Long roomId, User author, String content) {
        this.id = id; //테스트용
        this.roomId = roomId;
        this.author = author;
        this.content = content;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }
}
