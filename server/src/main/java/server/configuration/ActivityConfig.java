package server.configuration;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.service.ActivityService;
import server.service.GameService;

/**
 * Config class to add activies in local folder to database.
 * Once the annotations are uncommented, bean will be created and
 * will run once server Main class is executed.
 */
@Configuration
public class ActivityConfig {
    @Bean
    CommandLineRunner commandLineRunner(final ActivityService activityService, final GameService gameService) {
        return args -> {
            for (int i = 0; i <  12; i++) {

            }
            gameService.createSingleplayer(activityService.get());
        };
    }
}
