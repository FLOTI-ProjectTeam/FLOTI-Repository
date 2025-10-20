package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomDiscussionPostRepository {
    Page<DiscussionPosts> searchDiscussionPosts(String search, String sort, Pageable pageable);
}
