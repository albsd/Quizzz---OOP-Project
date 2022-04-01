package server.service;

import commons.Activity;
import commons.DBController;
import commons.Image;
import commons.Question;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.ActivityRepository;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Optional;
import java.util.LinkedHashMap;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final String activitiesPath = "./src/main/resources/activities";

    private final String resourcesPath = "./src/main/resources";

    @Autowired
    public ActivityService(final ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getActivities() {
        long count = activityRepository.count();
        List<Long> ids = ThreadLocalRandom.current().longs(1L, count + 1L)
                .distinct()
                .limit(100)
                .boxed()
                .collect(Collectors.toList());

        return activityRepository.findAllById(ids);
    }

    public synchronized List<Question> getQuestionList() {
        List<Activity> activityList = getActivities();
        return activityList.stream()
            .map((activity) -> {
                int questionType = (int) ((Math.random() * (3)));
                return  turnActivityIntoQuestion(activity, questionType, generateOptions(activityList));
            })
            .collect(Collectors.toList());
    }

    /**
     * Helper method to generate questions to decide which questions get generated.
     * @param activity
     * @param questionType
     * @param options
     * @return a random question of a random question type
     */
    // question type of 0 means number multiple choice
    public Question turnActivityIntoQuestion(final Activity activity, final int questionType,
            final List<Activity> options) {
        byte[] image = generateImageByteArray(activity.getPath());
        byte[][] images = new byte[3][];
        if (questionType == 1) {
            images = new byte[][] {generateImageByteArray(options.get(0).getPath()),
                                            generateImageByteArray(options.get(1).getPath()),
                                            generateImageByteArray(options.get(2).getPath())};
        }
        //byte[] optionImage = generateImageByteArray(options.get(1).getPath());
        return switch (questionType) {
            case 0 -> activity.getNumberMultipleChoiceQuestion(image);
            case 1 -> activity.getActivityMultipleChoiceQuestion(options, images);
            default -> activity.getFreeResponseQuestion(image);
        };
    }

    /**
     * Helper method to generate activity multiple choice questions.
     * @param allActivities
     * @return a list with 3 randomly picked activities
     */
    public List<Activity> generateOptions(final List<Activity> allActivities) {
        List<Activity> copy = new ArrayList<>(allActivities);
        Random random = new Random();
        Activity baseActivity = copy.get(random.nextInt(copy.size()));
        while (baseActivity.getEnergyConsumption() > 100000L) {
            baseActivity = copy.get(random.nextInt(copy.size()));
        }
        return getClosestActivities(baseActivity, copy);
    }

    /**
     * Helper method that gets closest activities in terms of energy consumption within a range of 10 in the list
     * sorted by its "distance" to the base activity's energy consumption.
     * @param baseActivity
     * @param activities
     * @return A list including the base activity and 2 randomly chosen activities that are close to the energy
     * consumption of the base activity
     */
    public List<Activity> getClosestActivities(final Activity baseActivity, final List<Activity> activities) {
        final int closeness = 10;
        Collections.sort(activities, (a, b) -> {
            long d1 = Math.abs(a.getEnergyConsumption() - baseActivity.getEnergyConsumption());
            long d2 = Math.abs(b.getEnergyConsumption() - baseActivity.getEnergyConsumption());
            return Long.compare(d1, d2);
        });
        Random random = new Random();
        List<Activity> options = new ArrayList<>();
        options.add(baseActivity);
        int firstActivityIndex = random.nextInt(closeness);
        while (baseActivity.equals(activities.get(firstActivityIndex))) {
            firstActivityIndex = random.nextInt(closeness);
        }
        options.add(activities.get(firstActivityIndex));
        int secondActivityIndex = random.nextInt(closeness);
        while (baseActivity.equals(activities.get(secondActivityIndex))
                || activities.get(secondActivityIndex).equals(activities.get(firstActivityIndex))) {
            secondActivityIndex = random.nextInt(closeness);
        }
        options.add(activities.get(secondActivityIndex));
        return options;
    }

    /**
     * Adds a new activity to the database.
     * @param activity Activity to be added
     * @return Activity added
     */
    public Activity addActivity(final Activity activity) {
        activity.setPath(resourcesPath + "/images/" + activity.getPath());
        return activityRepository.saveAndFlush(activity);
    }

    public List<Activity> addActivities(final List<Activity> activity) {
        return activityRepository.saveAllAndFlush(activity);
    }


    /**
     * Deletes an activity from the database and its image.
     * @param id ID of the activity to be deleted
     * @return Deleted activity
     */
    public Activity deleteActivity(final Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isEmpty()) {
            return null;
        } else {
            Activity activityReal = activity.get();
            (new File(activityReal.getPath())).delete();
            activityRepository.delete(activityReal);
            return activity.get();
        }
    }

    /**
     * Saves the image to the server.
     * @param image Image to be saved
     * @return Image saved
     * @throws IOException
     */
    public Image saveImage(final Image image) throws IOException {
        byte[] data = image.getData();
        String name = image.getName();
        File dir = new File(resourcesPath + "/images");
        if (!dir.exists()) dir.mkdirs();
        try {
            File file = new File(resourcesPath + "/images/" + name);
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public List<Image> saveImages(final List<Image> images) throws IOException {
        for (Image img : images) {
            saveImage(img);
        }
        return images;
    }

    /**
     * @param path Path of the image to be collected
     * @return Returns the Image that exists in the path
     */
    public Image getImage(final String path) {
        return new Image(generateImageByteArray(path), path.substring(path.lastIndexOf('/') + 1));
    }

    public List<Activity> getAllActivities() throws IOException, ParseException {
        List<Activity> activities = new ArrayList<>(activityRepository.findAll());
        if (activities.isEmpty()) {
            // unzipFolder();
            activities = populateRepo();
        }
        return activities;
    }

    @SuppressWarnings("unchecked")
    public List<Activity> populateRepo() throws FileNotFoundException, ParseException {
        List<Activity> activities = new ArrayList<>();
        List<File> files = DBController.getFiles(".json", new File(activitiesPath));
        for (File jsonFile : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
            JSONParser jsonParser = new JSONParser(bufferedReader);
            LinkedHashMap<String, ?> list = (LinkedHashMap<String, ?>) jsonParser.parse();
            String title = list.get("title").toString();
            long wattHours = ((BigInteger) list.get("consumption_in_wh")).longValue();
            String source = list.get("source").toString();

            String str = jsonFile.getName();
            File file = DBController.find(activitiesPath, str.substring(0, str.lastIndexOf('.')) + ".png");
            if (file == null) {
                file = DBController.find(activitiesPath, str.substring(0, str.lastIndexOf('.')) + ".jpeg");
            }
            if (file == null) {
                file = DBController.find(activitiesPath, str.substring(0, str.lastIndexOf('.')) + ".jpg");
            }
            String path = file.getPath().replaceAll("\\\\", "/");
            String realPath = path.substring(path.lastIndexOf("./src"));
            activities.add(new Activity(title, wattHours, source, realPath));
        }
        activityRepository.saveAllAndFlush(activities);
        return activities;
    }

    @SuppressWarnings("unused")
    private void unzipFolder() throws IOException {
        String fileZip = activitiesPath + ".zip";
        File destDir = new File(resourcesPath);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }
    }

    private byte[] generateImageByteArray(final String imagePath) {
        File file = new File(imagePath);
        String extension = imagePath.substring(imagePath.lastIndexOf('.') + 1);
        try {
            BufferedImage bImage = ImageIO.read(file);
            if (bImage == null) {
                URL imageURL = ActivityService.class.getClassLoader().getResource("icon.png");
                bImage = ImageIO.read(imageURL);
                extension = "png";
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, extension, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
