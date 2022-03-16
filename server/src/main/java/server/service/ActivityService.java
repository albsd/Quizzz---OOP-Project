package server.service;

import commons.Activity;
import commons.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import server.repository.ActivityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getActivities() {
        //Assumes 20 is the number of questions in the game
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            activities.add(this.randomActivity());
        }
        return activities;
    }

    public Activity randomActivity() {
        Long qty = activityRepository.count();
        int idx = (int) (Math.random() * qty);
        Page<Activity> activityPage = activityRepository.findAll(PageRequest.of(idx, 1));
        Activity a = null;
        if (activityPage.hasContent()) {
            a = activityPage.getContent().get(0);
        }
        return a;
    }

    public void addActivity(final Activity activity) {
        activityRepository.save(activity);
    }

    public List<Question> getQuestionList() {
        List<Activity> activityList = this.getActivities();
        List<Question> questionList = new ArrayList<>();


        for (int i = 0; i < 20; i++) {
            int questionType = (int) ((Math.random() * (3)));
            Question question = this.turnActivityIntoQuestion(activityList.get(i),
                    questionType, this.generateOptions(activityList, 3));
            questionList.add(question);
        }
        return questionList;
    }

    public Question turnActivityIntoQuestion(final Activity activity, final int questionType,
                                             final List<Activity> options) {
        //question type of 0 means number multiple choice
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
