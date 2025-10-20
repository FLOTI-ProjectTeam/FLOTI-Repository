package com.floti.api.domain.board.qna.dto;

import com.floti.api.domain.board.qna.entity.QnaPosts;
import lombok.Getter;

import java.util.List;

@Getter //테스트용
public class QnaPostDetailResponse extends QnaPostResponse {
    private final List<AnswerResponse> answers;

    public QnaPostDetailResponse(QnaPosts qnaPost, List<AnswerResponse> answers) {
        super(qnaPost);
        this.answers = answers;
    }
}
