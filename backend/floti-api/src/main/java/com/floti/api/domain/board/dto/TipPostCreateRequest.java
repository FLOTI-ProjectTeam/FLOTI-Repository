package com.floti.api.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //테스트용
public class TipPostCreateRequest {
    @NotNull
    private Long authorId; //임시

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;
}
