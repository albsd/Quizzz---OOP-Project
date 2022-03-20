package server.controller;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.ActivityService;

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
     * Adds the activity in the activity repo in the request body.
     *
     * @return the activity that was added
     */
    @GetMapping(path = {"", "/"})
    public List<Activity> getAllActivity() {
        return activityService.getActivities();
    }

    /**
     * Adds the activity in the activity repo in the request body.
     *
     * @param activity activity to be added.
     * @return the activity that was added
     */
    @PostMapping(path = {"", "/"})
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
