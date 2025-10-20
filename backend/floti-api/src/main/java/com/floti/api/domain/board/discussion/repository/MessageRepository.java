package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages, Long> {
    List<Messages> findTop50ByPostIdOrderByIdDesc(Long id);
}
