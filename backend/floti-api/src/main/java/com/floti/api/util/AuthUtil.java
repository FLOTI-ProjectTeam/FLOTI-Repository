package com.floti.api.util;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static org.springframework.security.core.userdetails.User.*;

@Component
@RequiredArgsConstructor
public final class AuthUtil {
    private final UserRepository userRepository;

    public User resolveUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }

    public Long resolveUserId(UserDetails userDetails) {
        return resolveUser(userDetails).getId();
    }

    public UserDetails loadUserByUsername(String username) {
        return withUsername(username) // 사용자 ID
                .password("N/A") // 비밀번호 미사용
                .authorities(Collections.singleton(() -> "ROLE_USER")) // 권한
                .build();
    }
}