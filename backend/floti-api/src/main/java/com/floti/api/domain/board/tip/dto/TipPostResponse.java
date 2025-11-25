package com.floti.api.domain.board.tip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class TipPostResponse {
    private final Long id;
    private final AuthorResponse author;
    private final String title;
    private final String content;
    private String thumbnail;
    private final int commentCount;
    private final int likeCount;
    private final boolean liked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private final LocalDateTime createdAt;

    public TipPostResponse(TipPosts tipPost, String baseUrl) {
        this(tipPost, baseUrl, false);
    }

    public TipPostResponse(TipPosts tipPost, String baseUrl, boolean liked) {
        this.id = tipPost.getId();
        this.author = new AuthorResponse(tipPost.getAuthor());
        this.title = tipPost.getTitle();
        this.content = tipPost.getContent();

        if (tipPost.getThumbnail() != null)
            this.thumbnail = baseUrl + "/" + tipPost.getThumbnail();

        this.commentCount = tipPost.getCommentCount();
        this.likeCount = tipPost.getLikeCount();
        this.createdAt = tipPost.getCreatedAt();
        this.liked = liked;
    }
}
