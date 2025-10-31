package com.floti.api.domain.board.discussion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.entity.Messages;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class MessageResponse {
    private final Long id;
    private final Long roomId;
    private final AuthorResponse author;
    private final String content;
    private final int likeCount;
    private final boolean liked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;

    public MessageResponse(Messages message, boolean liked) {
        this.id = message.getId();
        this.roomId = message.getRoomId();
        this.author = new AuthorResponse(message.getAuthor());
        this.content = message.getContent();
        this.likeCount = message.getLikeCount();
        this.createdAt = message.getCreatedAt();
        this.liked = liked;
    }
}
