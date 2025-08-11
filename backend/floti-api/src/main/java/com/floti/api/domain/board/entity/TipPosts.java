package com.floti.api.domain.board.entity;

import com.floti.api.temp.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipPosts {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @Setter
    @Column(nullable = false, length = 100)
    private String title;

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Setter
    @Column(length = 65)
    private String thumbnail; //경로: tip/thumbnail/날짜_UUID.확장자

    @Column(nullable = false)
    private Integer commentCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public TipPosts(Users author, String title, String content, String thumbnail) {
        this.author = author;
        this.title = title;
        this.content = content;
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
