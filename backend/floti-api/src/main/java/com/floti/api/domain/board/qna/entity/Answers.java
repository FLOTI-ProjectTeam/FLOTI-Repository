package com.floti.api.domain.board.qna.entity;

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
public class Answers implements LikeableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int likeCount;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Answers(Long id, Long postId, User author, String content) {
        this.id = id; //테스트용
        this.postId = postId;
        this.author = author;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }

    public void accept() {
        this.accepted = true;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }
}
