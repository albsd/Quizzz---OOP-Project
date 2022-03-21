package server.configuration;


import org.springframework.boot.CommandLineRunner;
import server.service.ActivityService;

//@Configuration
public class ActivityConfig {
//    @Bean
    CommandLineRunner commandLineRunner(final ActivityService activityService) {
        return args -> {
            activityService.populateRepo();
        };
    }
}
