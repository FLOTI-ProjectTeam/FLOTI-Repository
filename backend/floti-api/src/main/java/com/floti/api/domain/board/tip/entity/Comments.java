package com.floti.api.domain.board.tip.entity;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comments {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private Long postId;

    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likeCount;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Comments(Long id, Long postId, Long parentId, User author, String content) {
        this.id = id; //테스트용
        this.postId = postId;
        this.parentId = parentId;
        this.author = author;
        this.content = content;
    }

    @PrePersist //테스트용
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void update(CommentRequest request) {
        this.content = request.getContent();
    }

    public void softDelete() {
        this.deleted = true;
    }
}
