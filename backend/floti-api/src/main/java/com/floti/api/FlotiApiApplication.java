package com.floti.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlotiApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlotiApiApplication.class, args);
	}

	// [Fix] Startup Logic: Ensure user1 has the correct encrypted password for
	// "1234"
	// This bypasses any init.sql / Docker volume caching issues.
	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner initData(
			com.floti.api.domain.auth.repository.UserRepository userRepository,
			org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
		return args -> {
			userRepository.findByUsername("user1").ifPresent(user -> {
				user.setPassword(passwordEncoder.encode("1234"));
				userRepository.save(user);
				System.out.println(">>> [Fix] Forced user1 password update to '1234' (Encrypted)");
			});
		};
	}
}
