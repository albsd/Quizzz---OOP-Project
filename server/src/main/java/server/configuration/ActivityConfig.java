package server.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.service.ActivityService;


/**
 * Config class to generate list of questions when creating game objects.
 * This class is implemented once server starts.
 */
@Configuration
public class ActivityConfig {
    @Bean
    CommandLineRunner commandLineRunner(final ActivityService activityService) {
        return args -> {
            for (int i = 0; i < 10; i++) {
                activityService.generateQuestions();
            }
        };
    }
}
