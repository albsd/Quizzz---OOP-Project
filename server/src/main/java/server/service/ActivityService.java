package server.service;

import commons.Activity;
import commons.Question;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import server.repository.ActivityRepository;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.LinkedHashMap;
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
        byte[] image = generateImageByteArray(activity.getPath());
        Question question;
        if (questionType == 0) {
            question = activity.getNumberMultipleChoiceQuestion(image);
        } else if (questionType == 1) {
            question = activity.getActivityMultipleChoiceQuestion(options, image);
        } else {
            question = activity.getFreeResponseQuestion(image);
        }
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

    public Activity deleteActivity(final Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isEmpty()) {
            return null;
        } else {
            activityRepository.delete(activity.get());
            return activity.get();
        }
    }

    public List<Activity> getAllActivities() throws FileNotFoundException, ParseException {
        List<Activity> activities = new ArrayList<>(activityRepository.findAll());
        if (activities.isEmpty()) {
            List<File> files = getFiles(".json", new File("./server/src/main/resources/activities"));
            for (int i = 0; i < files.size(); i++) {
                File jsonFile = files.get(i);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
                JSONParser jsonParser = new JSONParser(bufferedReader);
                LinkedHashMap list = (LinkedHashMap) jsonParser.parse();
                String title = list.get("title").toString();
                long wattHours = ((BigInteger) list.get("consumption_in_wh")).longValue();
                String source = list.get("source").toString();

                String str = jsonFile.getName();
                File file = find("./server/src/main/resources/activities",
                        str.substring(0, str.lastIndexOf('.')) + ".png");
                if (file == null) {
                    file = find("./server/src/main/resources/activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                }
                if (file == null) {
                    file = find("./server/src/main/resources/activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpg");
                }
                //example:
                // /resources/activities/00/fridge.png
                String path = file.getPath().replaceAll("\\\\", "/");
                String realPath = path.substring(path.lastIndexOf("/resources"));
                activities.add(new Activity(title, wattHours, source, realPath));
            }
        }
        return activities;
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

    private static File find(final String path, final String fName) {
        File f = new File(path);
        if (fName.equalsIgnoreCase(f.getName())) return f;
        if (f.isDirectory()) {
            for (String aChild : f.list()) {
                File ff = find(path + File.separator + aChild, fName);
                if (ff != null) return ff;
            }
        }
        return null;
    }

    private static List<File> getFiles(final String ext, final File folder) {
        String extension = ext.toUpperCase();
        final List<File> files = new ArrayList<File>();
        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFiles(extension, file));
            } else if (file.getName().toUpperCase().endsWith(extension)) {
                files.add(file);
            }
        }
        return files;
    }
}
