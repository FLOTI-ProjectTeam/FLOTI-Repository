package com.floti.api.domain.board.qna.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter //테스트용
public class QnaPostResponse {
    private final Long id;
    private final AuthorResponse author;
    private final String title;
    private final String content;
    private final int answerCount;
    private final boolean accepted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public QnaPostResponse(QnaPosts qnaPost) {
        this.id = qnaPost.getId();
        this.author = new AuthorResponse(qnaPost.getAuthor());
        this.title = qnaPost.getTitle();
        this.content = qnaPost.getContent();
        this.answerCount = qnaPost.getAnswerCount();
        this.accepted = qnaPost.isAccepted();
        this.createdAt = qnaPost.getCreatedAt();
    }
}
