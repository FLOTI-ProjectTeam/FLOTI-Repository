package com.floti.api.domain.board.qna.entity;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.error.exception.PostAlreadyAcceptedException;
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
public class QnaPosts {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int answerCount;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public QnaPosts(User author, String title, String content, boolean accepted) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.accepted = accepted; //테스트용
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void accept(Answers answer) {
        if (this.accepted)
            throw new PostAlreadyAcceptedException();

        answer.accept();
        this.accepted = true;
    }

    public void incrementAnswerCount() {
        this.answerCount++;
    }

    public void decrementAnswerCount() {
        if (this.answerCount > 0) this.answerCount--;
    }
}
