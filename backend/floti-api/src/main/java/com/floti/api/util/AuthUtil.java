package com.floti.api.util;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;

    public User resolveUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }

    public Long resolveUserId(UserDetails userDetails) {
        return resolveUser(userDetails).getId();
    }
}