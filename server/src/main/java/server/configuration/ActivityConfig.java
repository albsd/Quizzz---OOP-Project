package server.configuration;


import org.springframework.boot.CommandLineRunner;
import server.service.ActivityService;

/**
 * Config class to add activies in local folder to database.
 * Once the annotations are uncommented, bean will be created and
 * will run once server Main class is executed.
 */
//@Configuration
public class ActivityConfig {
//    @Bean
    CommandLineRunner commandLineRunner(final ActivityService activityService) {
        return args -> {
            activityService.populateRepo();
        };
    }
}
