package com.floti.api.config;

import com.floti.api.domain.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test") // 테스트 환경에서는 실행되지 않도록 설정 (선택 사항, 필요 시 제거 가능)
public class DataInitConfig {

    // [Fix] Startup Logic: Ensure user1 has the correct encrypted password for
    // "1234"
    // This bypasses any init.sql / Docker volume caching issues.
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("user1").ifPresent(user -> {
                user.setPassword(passwordEncoder.encode("1234"));
                userRepository.save(user);
                System.out.println(">>> [Fix] Forced user1 password update to '1234' (Encrypted)");
            });
        };
    }
}
