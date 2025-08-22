package com.floti.api.auth.controller;


// 스프링/검증 관련 import
import com.floti.api.auth.dto.*;                     // 우리가 만든 DTO들
import com.floti.api.auth.entity.User;               // 사용자 엔티티
import com.floti.api.auth.repository.UserRepository; // 사용자 레포지토리
import com.floti.api.auth.service.UserService;       // 기존 회원가입 서비스
import com.floti.api.auth.code.InMemoryCodeService;  // 코드 서비스
import com.floti.api.auth.code.CodeType;             // 코드 타입
import com.floti.api.security.jwt.JwtUtil;           // JWT 유틸
import jakarta.validation.Valid;                     // @Valid 검증
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;      // 응답 객체
import org.springframework.security.crypto.password.PasswordEncoder; // 비번 인코더
import org.springframework.web.bind.annotation.*;     // 컨트롤러 어노테이션 전반

import java.awt.*;
import java.util.Optional;                           // Optional 처리

// 인증/계정 관련 엔드포인트를 담당하는 컨트롤러
@RestController // JSON 응답 컨트롤러
@RequestMapping("/auth") // 공통 URL prefix
public class AuthController {

    // 필요한 의존성들 주입
    private final UserService userService;           // 회원가입 로직(이미 구현됨)
    private final UserRepository userRepository;     // 사용자 조회/중복 체크
    private final PasswordEncoder passwordEncoder;   // 비번 검증/인코딩
    private final InMemoryCodeService codeService;   // 인증코드 발급/검증
    private final JwtUtil jwtUtil;                   // JWT 발급/검증

    // 생성자 주입(스프링이 자동으로 빈을 넣어줌)
    public AuthController(UserService userService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          InMemoryCodeService codeService,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.codeService = codeService;
        this.jwtUtil = jwtUtil;
    }

    // ───────────────── 회원가입 ─────────────────

    @PostMapping("/signup") // POST /auth/signup
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignUpRequestDto dto) {
        try {
            userService.signup(dto); // 기존 서비스 호출(중복 검사/인코딩/저장)
            return ResponseEntity.status(201).body(ApiResponse.ok(null, "회원가입 성공"));
        } catch (IllegalArgumentException e) {
            // 서비스가 던지는 예외 메시지에 따라 409로 변환(명세 반영)
            String msg = e.getMessage();
            if (msg != null && (msg.contains("이미 사용 중인 이메일") || msg.contains("이미 사용 중인 사용자 아이디"))) {
                return ResponseEntity.status(409).body(ApiResponse.fail(msg)); // 409 Conflict
            }
            return ResponseEntity.badRequest().body(ApiResponse.fail(msg)); // 기타는 400
        }
    }

    // ─────────── 회원가입 코드 전송/검증 ───────────

    @PostMapping("/signup/send-code") // POST /auth/signup/send-code
    public ResponseEntity<ApiResponse<Void>> sendSignupCode(@RequestBody @Valid EmailRequestDto req) {
        String code = codeService.issue(CodeType.SIGNUP, req.getEmail()); // 코드 발급
        // TODO: 실제 이메일 전송 API 연동(지금은 콘솔/로그 발송 가정)
        System.out.println("[DEBUG] SIGNUP CODE to " + req.getEmail() + " : " + code);
        return ResponseEntity.ok(ApiResponse.ok(null, "회원가입 코드 전송 완료(유효 5분)"));
    }

    @PostMapping("/signup/verify-code") // POST /auth/signup/verify-code
    public ResponseEntity<ApiResponse<Void>> verifySignupCode(@RequestBody @Valid CodeVerifyRequestDto req) {
        boolean ok = codeService.verify(CodeType.SIGNUP, req.getEmail(), req.getCode()); // 검증
        if (!ok) return ResponseEntity.badRequest().body(ApiResponse.fail("코드가 유효하지 않거나 만료되었습니다.")); // 400
        codeService.consume(CodeType.SIGNUP, req.getEmail()); // 사용 후 제거(선택)
        return ResponseEntity.ok(ApiResponse.ok(null, "코드 검증 성공"));
    }

    // ───────────────── 아이디 중복 확인 ─────────────────

    @PostMapping("/signup/check-username") // POST /auth/signup/check-username
    public ResponseEntity<ApiResponse<Void>> checkUsername(@RequestBody @Valid UsernameCheckRequestDto req) {
        boolean exists = userRepository.existsByUsername(req.getUsername()); // 중복 체크
        if (exists) return ResponseEntity.status(409).body(ApiResponse.fail("이미 사용 중인 사용자 이름입니다.")); // 409
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 사용자 이름입니다."));
    }

    // ───────────────── 로그인 ─────────────────

    @PostMapping("/login") // POST /auth/login
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto req) {
        // 1) username으로 사용자 조회
        Optional<User> opt = userRepository.findByUsername(req.getUsrename()); // username 정확히 일치하는 사용자 1명 조회

        // 2) 사용자가 없으면 401
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.fail("존재하지 않는 사용자입니다.")); // 401
        }

        // 3) 비밀번호 비교(BCrypt)
        User user = opt.get(); // DB에서 가져온 사용자
        // passwordEncoder.matches(평문, 해시) → true면 일치
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body(ApiResponse.fail("비밀번호가 올바르지 않습니다.")); // 401
        }
        // 4) JWT 발급(토큰 주체에 username 넣음)
        String token = jwtUtil.generateToken(user.getUsername());

        // 5) 응답 바디 구성 (명세: { jwt, nickname })
        LoginResponseDto body = new LoginResponseDto(token, user.getNickname());

        // 6) 200 OK 반환
        return ResponseEntity.ok(ApiResponse.ok(body, "로그인 성공"));

    }

    // ───────────────── 아이디 찾기 ─────────────────

    @PostMapping("/find-username") // POST /auth/find-username
    public ResponseEntity<ApiResponse<String>> findUsername(@RequestBody @Valid EmailRequestDto req) {
        Optional<User> found = userRepository.findByEmail(req.getEmail()); // 이메일로 조회
        if (found.isEmpty()) return ResponseEntity.status(404).body(ApiResponse.fail("해당 이메일의 계정을 찾을 수 없습니다.")); // 404
        return ResponseEntity.ok(ApiResponse.ok(found.get().getUsername(), "아이디 찾기 성공"));
    }

    // ─────────── 비밀번호 찾기: 코드 전송/검증/재설정 ───────────

    @PostMapping("/find-password/send-code") // POST /auth/find-password/send-code
    public ResponseEntity<ApiResponse<Void>> sendResetCode(@RequestBody @Valid ResetPasswordRequestDto req) {
        // 이메일 존재 여부 체크
        Optional<User> found = userRepository.findByEmail(req.getEmail());
        if (found.isEmpty()) return ResponseEntity.status(404).body(ApiResponse.fail("해당 이메일의 계정을 찾을 수 없습니다.")); // 404
        String code = codeService.issue(CodeType.RESET_PASSWORD, req.getEmail()); // 코드 발급
        System.out.println("[DEBUG] RESET CODE to " + req.getEmail() + " : " + code); // TODO 메일 전송
        return ResponseEntity.ok(ApiResponse.ok(null, "비밀번호 재설정 코드 전송 완료(유효 5분)"));
    }

    @PostMapping("/find-password/verify-code") // POST /auth/find-password/verify-code
    public ResponseEntity<ApiResponse<Void>> verifyResetCode(@RequestBody @Valid CodeVerifyRequestDto req) {
        boolean ok = codeService.verify(CodeType.RESET_PASSWORD, req.getEmail(), req.getCode()); // 검증
        if (!ok) return ResponseEntity.badRequest().body(ApiResponse.fail("코드가 유효하지 않거나 만료되었습니다.")); // 400
        return ResponseEntity.ok(ApiResponse.ok(null, "코드 검증 성공"));
    }

    @PatchMapping("/find-password/reset") // PATCH /auth/find-password/reset
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequestDto req) {
        // 코드 검증을 선행 요구하려면 verify 이후에만 허용하는 정책 필요(여기선 단순화)
        Optional<User> found = userRepository.findByEmail(req.getEmail());
        if (found.isEmpty()) return ResponseEntity.status(404).body(ApiResponse.fail("해당 이메일의 계정을 찾을 수 없습니다.")); // 404
        User user = found.get();
        user.setPassword(passwordEncoder.encode(req.getPassword())); // 새 비번 인코딩 저장
        userRepository.save(user); // 저장
        return ResponseEntity.ok(ApiResponse.ok(null, "비밀번호 재설정 완료"));
    }

}


