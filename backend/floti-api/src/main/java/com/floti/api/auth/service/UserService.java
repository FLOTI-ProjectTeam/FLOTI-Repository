package com.floti.api.auth.service;

import com.floti.api.auth.dto.SignUpRequestDto;
import com.floti.api.auth.entity.User;
import com.floti.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // 생성자 주입 자동 생성 (final 필드 자동 주입)
public class UserService {

    private final UserRepository userRepository; // DB 접근 개체
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 객체

    // 회원가입 처리 메서드
    public void signup(SignUpRequestDto requestDto) {
        // 1. 이메일 중복 검사
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 사용자 이름 중복 검사
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 아이디입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 4. User 엔터티로 변환
        User user = User.builder()
                .email(requestDto.getEmail())
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .profileImage(null) // 아직 업로드 안 했으므로 null
                .lastLogin(LocalDateTime.now()) // 현재 시간 저장
                .build();

        // 5. DB에 저장
        userRepository.save(user);
    }
}
