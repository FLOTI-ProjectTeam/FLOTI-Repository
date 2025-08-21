package com.floti.api.domain.board.qna.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //테스트용
public class QnaPostResponse {
    private final Long id;
    private final String authorNickname;
    private final String title;
    private final String content;
    private final Integer answerCount;
    private final boolean completed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public QnaPostResponse(QnaPosts qnaPost) {
        this.id = qnaPost.getId();
        this.authorNickname = qnaPost.getAuthor().getNickname();
        this.title = qnaPost.getTitle();
        this.content = qnaPost.getContent();
        this.answerCount = qnaPost.getAnswerCount();
        this.completed = qnaPost.isCompleted();
        this.createdAt = qnaPost.getCreatedAt();
    }
}
