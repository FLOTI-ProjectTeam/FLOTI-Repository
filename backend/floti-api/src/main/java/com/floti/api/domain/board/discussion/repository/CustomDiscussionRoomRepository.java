package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomDiscussionRoomRepository {
    Page<DiscussionRooms> searchDiscussionRooms(String search, String sort, Pageable pageable);
}
