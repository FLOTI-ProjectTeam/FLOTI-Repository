package com.floti.api.domain.board.tip.entity;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.common.entity.LikeableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipPosts implements LikeableEntity {
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

    @Column(length = 65)
    private String thumbnail; //경로: tip/thumbnail/날짜_UUID.확장자

    @Column(nullable = false)
    private Integer commentCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public TipPosts(Users author, String title, String content, String thumbnail) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
    }

    @PrePersist //테스트용
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void update(PostRequest post) {
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }
}
