package com.floti.api.domain.board.tip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.tip.entity.TipPosts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class TipPostResponse {
    private final Long id;
    private final String authorNickname;
    private final String title;
    private final String content;
    private final String thumbnail; //경로: tip/thumbnail/날짜_UUID.확장자
    private final Integer commentCount;
    private final Integer likeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private final LocalDateTime createdAt;

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
