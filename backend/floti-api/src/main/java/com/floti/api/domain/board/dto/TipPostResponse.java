package com.floti.api.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.entity.TipPosts;

import java.time.LocalDateTime;

public class TipPostResponse {
    private Long id;
    private String authorNickname;
    private String title;
    private String content;
    private String thumbnail; //경로: tip/thumbnail/날짜_UUID.확장자
    private Integer commentCount;
    private Integer likeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;

    public TipPostResponse(TipPosts tipPost) {
        this.id = tipPost.getId();
        this.authorNickname = tipPost.getAuthor().getNickname();
        this.title = tipPost.getTitle();
        this.content = tipPost.getContent();
        this.thumbnail = tipPost.getThumbnail();
        this.commentCount = tipPost.getCommentCount();
        this.likeCount = tipPost.getLikeCount();
        this.createdAt = tipPost.getCreatedAt();
    }
}
