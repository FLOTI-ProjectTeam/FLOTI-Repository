package com.floti.api.domain.board.qna.repository;

import com.floti.api.domain.board.qna.entity.QnaPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QnaPostRepository extends JpaRepository<QnaPosts, Long>, CustomQnaPostRepository {
    @Query("SELECT q FROM QnaPosts q WHERE (:unaccepted = false OR q.accepted = false)")
    Page<QnaPosts> findAll(boolean unaccepted, Pageable pageable);
}
