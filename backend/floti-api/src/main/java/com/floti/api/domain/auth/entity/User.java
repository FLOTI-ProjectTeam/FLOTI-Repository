package com.floti.api.domain.auth.entity;

import jakarta.persistence.*; // JPA 어노테이션
import lombok.*;             // Lombok 자동 생성기능
import java.time.LocalDateTime;

@Entity // 이 클래스가 DB 테이블과 연결됨
@Table(name = "users") // 실제 DB 테이블 이름
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id // 기본키(PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 320)
    private String email; // 이메일

    @Column(nullable = false, unique = true, length = 10)
    private String username; // 아이디

    @Column(nullable = false, length = 60)
    private String password; // 비밀번호

    @Column(nullable = false, length = 15)
    private String nickname; // 닉네임

    @Column(name = "profile_image", length = 50)
    private String profileImage; // 프로필 이미지 경로

    @Column(name = "last_login")
    private LocalDateTime lastLogin; // 마지막 로그인 시간
}