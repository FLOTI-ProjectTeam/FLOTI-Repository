package com.floti.api.domain.board.tip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //테스트용
public class CommentRequest {
    private Long parentId;
    private Long authorId; //임시

    @NotBlank
    @Size(max = 255)
    private String content;
}
