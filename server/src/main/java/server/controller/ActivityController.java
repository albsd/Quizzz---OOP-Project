package server.controller;

import commons.Activity;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import server.service.ActivityService;
import java.io.IOException;
import java.util.List;

/**
 * Endpoint to configure Activity repository.
 * Allow clients to add or remove activities in the repository.
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(final ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Gets all the activities currently inside the database.
     *
     * @return all activities
     */
    @GetMapping(path = {"", "/"})
    public List<Activity> getAllActivity() throws IOException, ParseException {
        return activityService.getAllActivities();
    }

    /**
     * Adds the activity in the activity repo in the request body.
     *
     * @param activity activity to be added.
     * @return the activity that was added
     */
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<Activity> addActivity(final @RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.addActivity(activity));
    }

    /**
     * removes activity in repo based on id from parameter.
     *
     * @param id Long id
     * @return the activity that was deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Activity> deleteActivity(final @PathVariable Long id) {
        return ResponseEntity.ok(activityService.deleteActivity(id));
    }
}
