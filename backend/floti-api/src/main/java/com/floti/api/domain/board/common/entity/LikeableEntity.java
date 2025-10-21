package com.floti.api.domain.board.common.entity;

import com.floti.api.domain.auth.entity.User;

public interface LikeableEntity {
    Long getId();
    User getAuthor();
    void incrementLikeCount();
    void decrementLikeCount();
    int getLikeCount();
}