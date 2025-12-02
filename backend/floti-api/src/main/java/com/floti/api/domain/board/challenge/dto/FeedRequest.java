package com.floti.api.domain.board.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 챌린지 피드 작성 및 수정 요청 DTO.
 */
@Getter
@Setter
public class FeedRequest {
    /**
     * 피드 내용. 공백을 허용하지 않으며 최대 255자까지 입력할 수 있다.
     */
    @NotBlank
    @Size(max = 255)
    private String content;
}