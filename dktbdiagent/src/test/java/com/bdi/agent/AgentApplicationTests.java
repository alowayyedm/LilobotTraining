package com.bdi.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
<<<<<<< HEAD

@SpringBootTest
=======
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
>>>>>>> origin/updatedLilo
class AgentApplicationTests {

	@Test
	void contextLoads() {
	}

}
