package com.floti.api.domain.like.entity;

import com.floti.api.domain.board.common.entity.UserPostId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(UserPostId.class)
public class LikeTipPosts {
    @Id private Long userId;
    @Id private Long postId;
}
