package nz.ac.canterbury.seng302.gardenersgrove;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ApplicationController;


@SpringBootTest
class GardenersGroveApplicationTests {

	@Autowired
	private ApplicationController applicationController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(applicationController);
	}

}

