package com.floti.api.domain.board.common.dto;

import com.floti.api.domain.auth.entity.Users;
import lombok.Getter;

@Getter //테스트용
public class AuthorResponse {
    private final Long id;
    private final String nickname;
    private final String profileImage;

    public AuthorResponse(Users author) {
        this.id = author.getId();
        this.nickname = author.getNickname();
        this.profileImage = author.getProfileImage();
    }
}
