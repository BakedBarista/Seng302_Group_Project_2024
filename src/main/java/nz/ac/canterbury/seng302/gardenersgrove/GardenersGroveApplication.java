package nz.ac.canterbury.seng302.gardenersgrove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Gardener's Grove entry-point
 * Note @link{SpringBootApplication} annotation
 * 
 * <p>
 * Also note the @link{EnableScheduling} annotation, which is used to
 * periodically check for expired tokens.
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
public class GardenersGroveApplication {

	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(GardenersGroveApplication.class, args);
	}

}
