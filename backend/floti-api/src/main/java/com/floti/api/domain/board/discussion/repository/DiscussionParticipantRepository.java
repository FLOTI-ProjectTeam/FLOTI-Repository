package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.DiscussionParticipants;
import com.floti.api.domain.board.discussion.entity.PostParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiscussionParticipantRepository  extends JpaRepository<DiscussionParticipants, PostParticipantId> {
    @Query("SELECT dp FROM DiscussionParticipants dp JOIN FETCH dp.participant WHERE dp.postId = :postId")
    List<DiscussionParticipants> findByPostId(@Param("postId") Long postId);
}
