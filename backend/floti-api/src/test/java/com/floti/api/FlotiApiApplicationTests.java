package com.floti.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // 무조건 test yml을 사용
class FlotiApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
