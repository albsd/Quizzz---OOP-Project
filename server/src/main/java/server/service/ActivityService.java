package server.service;

import commons.Activity;
import commons.MultipleChoiceQuestion;
import commons.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.ActivityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ActivityService {

    private ActivityRepository activityRepository;

    @Autowired
    public ActivityService(final ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getActivities() {
        // Assumes 20 is the number of questions in the game
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            activities.add(this.randomActivity());
        }
        return activities;
    }

    public Activity randomActivity() {
        Long qty = activityRepository.count();
        int idx = (int) (Math.random() * qty);
        var all = activityRepository.findAll();
        if (all.size() == 0) return null;
        return all.get(idx % all.size());
    }

    public void addActivity(final Activity activity) {
        activityRepository.save(activity);
    }

    public List<Question<?>> getQuestionList() {
        List<Activity> activityList = this.getActivities();
        List<Question<?>> questionList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            int questionType = (int) ((Math.random() * (3)));
            Question<?> question = this.turnActivityIntoQuestion(activityList.get(i),
                    questionType, this.generateOptions(activityList, 3));
            questionList.add(question);
        }
        return questionList;
    }

    public Question<?> turnActivityIntoQuestion(final Activity activity, final int questionType,
            final List<Activity> options) {
        if (activity == null) {
            String[] ops = new String[] {"a", "b", "c"};
            return new MultipleChoiceQuestion("What is the first letter of the alphabet?", new byte[0], ops, "a");
        }
        // question type of 0 means number multiple choice
        return switch (questionType) {
            case 0 -> activity.getNumberMultipleChoiceQuestion();
            case 1 -> activity.getActivityMultipleChoiceQuestion(options);
            default -> activity.getFreeResponseQuestion();
        };
    }

    public List<Activity> generateOptions(final List<Activity> allActivities, final int numberOfOptions) {
        List<Activity> copy = new ArrayList<Activity>(allActivities);
        Collections.shuffle(copy);
        return numberOfOptions > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, numberOfOptions);
    }
}
