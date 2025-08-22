package com.floti.api.domain.board.tip.entity;

import com.floti.api.domain.auth.entity.Users;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private Long postId;

    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Users author;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Comments(Long postId, Long parentId, Users author, String content) {
        this.postId = postId;
        this.parentId = parentId;
        this.author = author;
        this.content = content;
    }

    public void update(CommentRequest comment) {
        this.content = comment.getContent();
    }

    public void softDelete() {
        this.deleted = true;
    }
}
