package server;

import commons.Activity;
import commons.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        activityRepository.findAll().forEach(activities::add);
        return activities;
    }

    public void addActivity(final Activity activity) {
        activityRepository.save(activity);
    }

    public List<Question> getQuestionList() {
        List<Activity> allActivities = this.getAllActivities();
        List<Activity> activityList = new ArrayList<>();
        int max = 0, min = allActivities.size();
        for (int i = 0; i < 20; i++) {
            int activityNumber = (int) ((Math.random() * (max - min)) + min);
            activityList.add(allActivities.get(activityNumber));
        }
    }

    public Question turnActivityIntoQuestion(final Activity activity, final int questionType) {
        //question type of 0 means number multiple choice
        if (questionType == 0) {
            return activity.getNumberMultipleChoiceQuestion();
        }
        else if (questionType == 1) {
            return activity.getActivityMultipleChoiceQuestion();
        }
        else {
            return activity.getFreeResponseQuestion();
        }
    }
}
