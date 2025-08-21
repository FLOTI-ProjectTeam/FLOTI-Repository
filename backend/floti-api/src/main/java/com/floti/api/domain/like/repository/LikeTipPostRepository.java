package com.floti.api.domain.like.repository;

import com.floti.api.domain.board.common.entity.UserPostId;
import com.floti.api.domain.like.entity.LikeTipPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeTipPostRepository extends JpaRepository<LikeTipPosts, UserPostId> {
}
