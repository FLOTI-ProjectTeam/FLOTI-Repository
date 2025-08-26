package com.floti.api.domain.board.tip.repository;

import com.floti.api.domain.board.tip.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByPostId(Long postId);

    Optional<Comments> findByIdAndPostId(Long id, Long postId);
}
