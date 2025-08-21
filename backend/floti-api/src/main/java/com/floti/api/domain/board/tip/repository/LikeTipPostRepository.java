package com.floti.api.domain.board.tip.repository;

import com.floti.api.domain.board.common.entity.UserPostId;
import com.floti.api.domain.board.tip.entity.LikeTipPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeTipPostRepository extends JpaRepository<LikeTipPosts, UserPostId> {
}
