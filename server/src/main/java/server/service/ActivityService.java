package server.service;

import commons.Activity;
import commons.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import server.repository.ActivityRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        Question question;
        if (questionType == 0) {
            question = activity.getNumberMultipleChoiceQuestion();
        } else if (questionType == 1) {
            question = activity.getActivityMultipleChoiceQuestion(options);
        } else {
            question = activity.getFreeResponseQuestion();
        }
        byte[] image = generateImageByteArray(activity.getPath());
        question.setImage(image);
        return question;
    }

    public List<Activity> generateOptions(final List<Activity> allActivities, final int numberOfOptions) {
        List<Activity> copy = new ArrayList<Activity>(allActivities);
        Collections.shuffle(copy);
        return numberOfOptions > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, numberOfOptions);
    }
    public Activity addActivity(final Activity activity) {
        return activityRepository.saveAndFlush(activity);
    }

//    public Activity addActivity(final Activity activity) {
//        Optional<Activity> optionalAct = activityRepository.findTopByOrderByIdDesc();
//        long id;
//        if (optionalAct.isEmpty()) {
//            id = 1L;
//        } else {
//            id = optionalAct.get().getId() + 1;
//        }
//        activity.setId(id);
//        return activityRepository.saveAndFlush(activity);
//    }

    public Activity deleteActivity(final Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isEmpty()) {
            return null;
        } else {
            activityRepository.delete(activity.get());
            return activity.get();
        }
    }

    private byte[] generateImageByteArray(final String imagePath) {
        //example of image path
        //"/resources/images/title.jpg";
        File file = new File(imagePath);
        String extension = imagePath.substring(imagePath.lastIndexOf('.') + imagePath.length());
        try {
            BufferedImage bImage = ImageIO.read(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, extension, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
            return new byte[0];
        }
    }
}
