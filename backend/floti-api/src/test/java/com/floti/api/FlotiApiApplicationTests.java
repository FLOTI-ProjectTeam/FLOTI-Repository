package com.floti.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // H2 프로필로 실행
class FlotiApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
