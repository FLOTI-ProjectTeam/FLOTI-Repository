package com.floti.api.domain.board.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter //테스트용
@AllArgsConstructor
public class LikeResponse {
    private final boolean liked;
    private final Integer likeCount;
}
