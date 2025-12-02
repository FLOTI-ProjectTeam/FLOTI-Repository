package com.floti.api.domain.board.challenge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 챌린지 생성 및 수정 시 클라이언트로부터 전달받는 요청 DTO.
 * 각 필드는 유효성 검사를 위해 어노테이션을 활용한다.
 */
@Getter
@Setter
public class ChallengeRequest {
    /**
     * 챌린지 제목. 공백을 허용하지 않으며 최대 100자까지 가능하다.
     */
    @NotBlank
    @Size(max = 100)
    private String title;

    /**
     * 한 줄 소개. 공백을 허용하지 않으며 최대 50자까지 가능하다.
     */
    @NotBlank
    @Size(max = 50)
    private String intro;

    /**
     * 챌린지 설명. 공백을 허용하지 않으며 최대 255자까지 가능하다.
     */
    @NotBlank
    @Size(max = 255)
    private String content;

    /**
     * 최대 참가 인원. 최소 2명, 최대 10명까지 설정할 수 있다.
     */
    @Min(2)
    @Max(10)
    private int maxParticipants;

    /**
     * 챌린지 시작일. 반드시 전달되어야 한다.
     */
    @NotNull
    private LocalDateTime startDate;

    /**
     * 챌린지 종료일. 반드시 전달되어야 한다.
     */
    @NotNull
    private LocalDateTime endDate;
}