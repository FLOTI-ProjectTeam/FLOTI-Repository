package com.floti.api.domain.board.qna.repository;

import com.floti.api.domain.board.qna.entity.QnaPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaPostRepository extends JpaRepository<QnaPosts, Long>, CustomQnaPostRepository {
    Page<QnaPosts> findByTitleContainingIgnoreCase(Pageable pageable, String search);
}
