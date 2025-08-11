package com.floti.api.domain.board.repository;

import com.floti.api.domain.board.entity.TipPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipPostRepository extends JpaRepository<TipPosts, Long> {
    /* 1. 검색 */
    Page<TipPosts> findByTitleContainingIgnoreCase(Pageable pageable, String search);
}
