package com.floti.api.domain.board.like.repository;

import com.floti.api.domain.board.like.entity.LikeComments;
import com.floti.api.domain.board.like.entity.UserCommentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeCommentRepository extends JpaRepository<LikeComments, UserCommentId> {
    List<LikeComments> findByUserIdAndCommentIdIn(Long userId, List<Long> commentIds);
}
