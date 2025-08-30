package com.floti.api.domain.board.qna.repository;

import com.floti.api.domain.board.qna.entity.QnaPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomQnaPostRepository {
    Page<QnaPosts> searchQnaPosts(String search, String sort, Pageable pageable);
}
