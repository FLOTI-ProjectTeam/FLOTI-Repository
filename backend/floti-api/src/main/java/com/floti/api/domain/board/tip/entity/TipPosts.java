package com.floti.api.domain.board.tip.entity;

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
public class TipPosts implements LikeableEntity {
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentPlain; // 검색 최적화를 위해 마크다운을 제거한 content

    @Column(length = 65)
    private String thumbnail; //경로: tips/thumbnail/날짜_UUID.확장자

    @Column(nullable = false)
    private int commentCount;

    @Column(nullable = false)
    private int likeCount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public TipPosts(User author, String title, String content, String contentPlain) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.contentPlain = contentPlain;
    }

    public void update(String title, String content, String contentPlain) {
        this.title = title;
        this.content = content;
        this.contentPlain = contentPlain;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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
