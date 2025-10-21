package com.floti.api.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter //테스트용
@AllArgsConstructor
public class LikeResponse {
    private final Long id;
    private final boolean liked;
    private final int likeCount;
}
