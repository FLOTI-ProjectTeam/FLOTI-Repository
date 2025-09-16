package com.floti.api.domain.board.tip.repository;

import com.floti.api.domain.board.tip.entity.TipPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 1. 인터페이스는 인터페이스만 상속한다.
 * 2. Repository는 Entity를 반환해야 한다.
 */
public interface CustomTipPostRepository {
    Page<TipPosts> searchTipPosts(String search, String sort, Pageable pageable);
}