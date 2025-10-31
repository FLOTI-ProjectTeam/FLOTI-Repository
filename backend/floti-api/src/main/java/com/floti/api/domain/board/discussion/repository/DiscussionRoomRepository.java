package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRoomRepository extends JpaRepository<DiscussionRooms, Long>, CustomDiscussionRoomRepository {
}
