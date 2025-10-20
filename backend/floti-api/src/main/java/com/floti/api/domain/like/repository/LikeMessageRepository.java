package com.floti.api.domain.like.repository;

import com.floti.api.domain.like.entity.LikeMessages;
import com.floti.api.domain.like.entity.UserMessageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeMessageRepository extends JpaRepository<LikeMessages, UserMessageId> {
    List<LikeMessages> findByUserIdAndMessageIdIn(Long userId, List<Long> messageIds);
}
