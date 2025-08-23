package com.floti.api.domain.board.qna.repository;

import com.floti.api.domain.board.qna.entity.Answers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answers, Long> {
    List<Answers> findByPostId(Long id);
}
