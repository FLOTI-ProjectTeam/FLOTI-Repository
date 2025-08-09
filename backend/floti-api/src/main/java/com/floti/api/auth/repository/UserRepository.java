package com.floti.api.auth.repository;

import com.floti.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
 // 이메일로 유저를 찾는 메서드
 Optional<User> findByEmail(String email);

 // username 중복 체크 용도
 boolean existsByUsername(String username);

 // 이메일 중복 체크 용도
 boolean existsByEmail(String email);
}
