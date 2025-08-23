package com.floti.api.domain.board.qna.entity;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.qna.dto.AnswerRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Users author;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Answers(Long postId, Users author, String content) {
        this.postId = postId;
        this.author = author;
        this.content = content;
    }

    public void update(AnswerRequest answer) {
        this.content = answer.getContent();
    }

    public void accept() {
        this.accepted = true;
    }
}
