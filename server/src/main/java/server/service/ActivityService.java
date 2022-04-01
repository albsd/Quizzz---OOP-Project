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
import java.util.Optional;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.LinkedHashMap;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final String activitiesPath = "./src/main/resources/activities";

    private final String resourcesPath = "./src/main/resources";

    private final float offset = 0.1f;

    private final String defaultImagePath = ".src/main/resources/images/icon.png";

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
                int questionType = (int) ((Math.random() * (4)));
                return  turnActivityIntoQuestion(activity, questionType, activityList);
            })
            .collect(Collectors.toList());
    }

    /**
     * questionType cases.
     * case 0 -> Multiple choice question that asks "How much energy consumption does X have?"
     * case 1 -> Multiple choice question that asks "Which of the activities have the most energy consumption?"
     * case 2 -> Multiple choice question that asks "Instead of doing X you can do..."
     * case 3 -> Free response question that asks "What is the energy consumption of X?"
     * @param activity Activity that is based on to create a question
     * @param questionType Question type that should be generated
     * @param activityList List of all activities available in the database
     * @return Returns a question that is based on the "activity" with the type determined by questionType param
     */
    public Question turnActivityIntoQuestion(final Activity activity, final int questionType,
            final List<Activity> activityList) {
        byte[] image;
        byte[][] images;
        List<Activity> options;

        return switch (questionType) {
            case 0 -> {
                image = generateImageByteArray(activity.getPath());
                yield activity.generateNumberMultipleChoiceQuestion(image);
            }
            case 1 -> {
                options = generateOptions(activityList);
                images = new byte[][] {generateImageByteArray(options.get(0).getPath()),
                        generateImageByteArray(options.get(1).getPath()),
                        generateImageByteArray(options.get(2).getPath())};
                yield activity.generateActivityMultipleChoiceQuestion(options, images);
            }

            case 2 -> {
                options = generateApproximateOptions(activityList, 3, activity);
                image = generateImageByteArray(activity.getPath());
                yield activity.generateInsteadOfMultipleChoiceQuestion(options, image);
            }

            default -> {
                image = generateImageByteArray(activity.getPath());
                yield activity.getFreeResponseQuestion(image);
            }
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
     * @param allActivities List of all activities available in the database
     * @param numberOfOptions Number of options that will be generated for the question
     * @param activity Activity that is based on to create a question
     * @return Returns a list of activities that will be used as options for the InsteadOf question type
     */
    public List<Activity> generateApproximateOptions(final List<Activity> allActivities,
                                                     final int numberOfOptions, final Activity activity) {

        // Getting activities that have approximately same energy consumption with the activity in the question
        List<Activity> approximateActivities = getApproximateActivities(allActivities, activity);

        List<Activity> options = new ArrayList<>();
        Random rand = new Random();

        // If there is an approximate activity, add it to the options
        Activity approximateActivity = null;
        if (!approximateActivities.isEmpty()) {
            approximateActivity = approximateActivities.get(rand.nextInt(approximateActivities.size()));
            options.add(approximateActivity);
        }

        // Fill options with incorrect activities
        fillIncorrectActivities(allActivities, options, activity, numberOfOptions);

        // Creating the "None" activity
        Activity noneActivity = new Activity();
        noneActivity.setTitle("None of the below");
        // Shuffling the options
        Collections.shuffle(options);

        // Answer is hidden/encoded in the energyConsumption of none activity
        // +1 is caused by adding the None at the beginning of the list
        if (approximateActivity != null) {
            noneActivity.setEnergyConsumption(options.indexOf(approximateActivity) + 1);
        } else {
            noneActivity.setEnergyConsumption(0);
        }
        // Inserting the None option at index 0, so it will always  be the top option
        options.add(0, noneActivity);
        return options;
    }

    /**
     * @param allActivities List of all activities available in the database
     * @param activity Activity that is based on to find approximate activities
     * @return Returns activities that have a similar energy consumption with the "activity" param as a list
     */
    private List<Activity> getApproximateActivities(final List<Activity> allActivities, final Activity activity) {
        return allActivities
                .stream()
                .filter(a -> !a.getTitle().equals(activity.getTitle()))
                .filter(a -> a.getEnergyConsumption() >= activity.getEnergyConsumption() * (1 - offset)
                        && a.getEnergyConsumption() <= activity.getEnergyConsumption() * (1 + offset))
                .collect(Collectors.toList());
    }

    /**
     * Fills the options list with activities that are not close to the "activity" param.
     * @param allActivities List of all activities available in the database
     * @param options List of activities that includes options for the InsteadOf question type
     * @param activity Activity that is based on to create a question
     * @param numberOfOptions Number of options that will be generated for the question
     */
    private void fillIncorrectActivities(final List<Activity> allActivities, final List<Activity> options,
                                        final Activity activity, final int numberOfOptions) {
        Random rand = new Random();
        while (options.size() < numberOfOptions - 1) {
            int index = rand.nextInt(allActivities.size());
            Activity incorrectActivity = allActivities.get(index);
            if (!options.contains(incorrectActivity)
                    && (incorrectActivity.getEnergyConsumption() < activity.getEnergyConsumption() * (1 - offset)
                    || incorrectActivity.getEnergyConsumption() > activity.getEnergyConsumption() * (1 + offset))) {
                options.add(incorrectActivity);
            }
        }
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
