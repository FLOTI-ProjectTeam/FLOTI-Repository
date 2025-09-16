package com.floti.api.domain.board.like.repository;

import com.floti.api.domain.board.like.entity.LikeAnswers;
import com.floti.api.domain.board.like.entity.UserAnswerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeAnswerRepository extends JpaRepository<LikeAnswers, UserAnswerId> {
    List<LikeAnswers> findByUserIdAndAnswerIdIn(Long userId, List<Long> answerIds);
}
