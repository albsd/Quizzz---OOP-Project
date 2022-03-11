package server;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        activityRepository.findAll().forEach(activities::add);
        return activities;
    }

    public void addActivity(Activity activity) {
        activityRepository.save(activity);
    }
}
