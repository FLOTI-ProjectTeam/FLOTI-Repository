package com.floti.api.domain.board.common.entity;

public interface LikeableEntity {
    void incrementLikeCount();
    void decrementLikeCount();
    int getLikeCount();
}