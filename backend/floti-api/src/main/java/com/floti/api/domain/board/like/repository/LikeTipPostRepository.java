package com.floti.api.domain.board.like.repository;

import com.floti.api.domain.board.like.entity.UserPostId;
import com.floti.api.domain.board.like.entity.LikeTipPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeTipPostRepository extends JpaRepository<LikeTipPosts, UserPostId> {
    boolean existsByUserIdAndPostId(Long userId, Long id);
}
