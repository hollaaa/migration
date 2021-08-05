package com.hansum.migration;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@Slf4j
@SpringBootTest
class MigrationApplicationTests {

	@Test
	void contextLoads() {

		log.debug("MigrationApplicationTests");
	}

}
