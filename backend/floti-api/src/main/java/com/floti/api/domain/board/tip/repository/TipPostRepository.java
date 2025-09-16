package com.floti.api.domain.board.tip.repository;

import com.floti.api.domain.board.tip.entity.TipPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipPostRepository extends JpaRepository<TipPosts, Long>, CustomTipPostRepository {
}
