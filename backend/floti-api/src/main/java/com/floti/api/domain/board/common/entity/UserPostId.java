package com.floti.api.domain.board.common.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@AllArgsConstructor
@EqualsAndHashCode
public class UserPostId implements Serializable {
    private Long userId;
    private Long postId;
}
