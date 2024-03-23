package nz.ac.canterbury.seng302.gardenersgrove;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public Clock clock() { 
        return Clock.system(ZoneId.of("Pacific/Auckland"));
    }
}
