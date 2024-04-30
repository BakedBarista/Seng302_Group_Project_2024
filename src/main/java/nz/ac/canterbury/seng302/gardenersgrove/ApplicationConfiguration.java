package nz.ac.canterbury.seng302.gardenersgrove;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Profile("!integrationTest") // See https://www.baeldung.com/spring-test-disable-enablescheduling
public class ApplicationConfiguration {
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Pacific/Auckland"));
    }
}
