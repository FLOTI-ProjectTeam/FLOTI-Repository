package com.floti.api.domain.board.qna.entity;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.common.dto.PostRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaPosts {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer answerCount = 0;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public QnaPosts(Users author, String title, String content, Boolean completed) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.completed = completed;
    }

    public void update(PostRequest post) {
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    public void incrementAnswerCount() {
        this.answerCount++;
    }

    public void decrementAnswerCount() {
        if (this.answerCount > 0) this.answerCount--;
    }
}
