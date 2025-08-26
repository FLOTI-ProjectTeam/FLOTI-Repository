package com.floti.api.domain.board.like.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCommentId implements Serializable {
    private Long userId;
    private Long commentId;
}
