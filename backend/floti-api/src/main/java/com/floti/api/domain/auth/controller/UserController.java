package com.floti.api.domain.auth.controller;

// import
import com.floti.api.domain.auth.dto.ApiResponse;             // 공통 응답
import com.floti.api.domain.auth.dto.ChangePasswordRequestDto; // 비번 변경 DTO
import com.floti.api.domain.auth.entity.User;                  // 유저 엔티티
import com.floti.api.domain.auth.repository.UserRepository;    // 유저 레포
import jakarta.validation.Valid;                        // @Valid
import org.springframework.http.ResponseEntity;         // 응답
import org.springframework.security.core.Authentication; // 인증 정보
import org.springframework.security.core.userdetails.UserDetails; // 사용자 정보
import org.springframework.security.crypto.password.PasswordEncoder; // 비번 인코더
import org.springframework.web.bind.annotation.*;        // 컨트롤러

import java.util.Optional;                              // Optional

@RestController // JSON 컨트롤러
@RequestMapping("/user") // prefix
public class UserController {

    private final UserRepository userRepository;   // DB 접근
    private final PasswordEncoder passwordEncoder; // 비번 인코딩

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 비밀번호 변경(로그인한 본인만)
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequestDto req,
            Authentication authentication
    ) {
        // 현재 로그인한 username 추출
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String username = principal.getUsername();

        // DB에서 해당 username 찾기
        Optional<User> opt = userRepository.findByUsername(username);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.fail("계정을 찾을 수 없습니다."));
        }

        User user = opt.get();
        user.setPassword(passwordEncoder.encode(req.getPassword())); // 새 비밀번호 인코딩
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.ok(null, "비밀번호 변경 완료"));
    }

    // 회원 탈퇴 (로그인한 본인만)
    @DeleteMapping("/withdraw")
    public  ResponseEntity<ApiResponse<Void>> withdraw(Authentication authentication) {
        // 현재 로그인한 username 추출
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String username = principal.getUsername();

        Optional<User> opt = userRepository.findByUsername(username);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.fail("계정을 찾을 수 없습니다."));
        }

        userRepository.delete(opt.get());

        return ResponseEntity.ok(ApiResponse.ok(null, "회원탈퇴 완료"));
    }
}
