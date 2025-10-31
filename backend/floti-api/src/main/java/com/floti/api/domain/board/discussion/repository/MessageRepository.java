package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages, Long> {
    List<Messages> findTop50ByRoomIdOrderByIdDesc(Long id);

    List<Messages> findTop50ByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long lastId);
}
