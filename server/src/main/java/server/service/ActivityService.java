package server.service;

import commons.Activity;
import commons.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.ActivityRepository;

import java.util.ArrayList;
import java.util.Collections;
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

    public void addActivity(final Activity activity) {
        activityRepository.save(activity);
    }

    public List<Question> getQuestionList() {
        List<Activity> allActivities = this.getAllActivities();
        List<Activity> activityList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>();
        int max = 0, min = allActivities.size();
        for (int i = 0; i < 20; i++) {
            int activityNumber = (int) ((Math.random() * (max - min)) + min);
            activityList.add(allActivities.get(activityNumber));
        }
        for (int i = 0; i < 20; i++) {
            int questionType = (int) ((Math.random() * (3)));
            Question question = this.turnActivityIntoQuestion(activityList.get(i),
                    questionType, this.generateOptions(allActivities, 3));
            questionList.add(question);
        }
        return questionList;
    }

    public Question turnActivityIntoQuestion(final Activity activity, final int questionType,
                                             final List<Activity> options) {
        //question type of 0 means number multiple choice
        System.out.println(questionType);
        if (questionType == 0) {
            return activity.getNumberMultipleChoiceQuestion();
        } else if (questionType == 1) {
            return activity.getActivityMultipleChoiceQuestion(options);
        } else {
            return activity.getFreeResponseQuestion();
        }
    }

    public List<Activity> generateOptions(final List<Activity> allActivities, final int numberOfOptions) {
        List<Activity> copy = new ArrayList<Activity>(allActivities);
        Collections.shuffle(copy);
        return numberOfOptions > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, numberOfOptions);
    }
}
