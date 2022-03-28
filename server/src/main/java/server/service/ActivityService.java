package server.service;

import commons.Activity;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final String activitiesPath = "./src/main/resources/activities";

    private final String resourcesPath = "./src/main/resources";

    @Autowired
    public ActivityService(final ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // Assumes 20 is the number of questions in the game
    public List<Activity> getActivities() {
        long count = activityRepository.count();
        List<Long> ids = ThreadLocalRandom.current().longs(1L, count + 1L)
                .distinct()
                .limit(20)
                .boxed()
                .collect(Collectors.toList());

        return activityRepository.findAllById(ids);
    }

    public List<Question> getQuestionList() {
        List<Activity> activityList = getActivities();
        return activityList.stream()
            .map((activity) -> {
                int questionType = (int) ((Math.random() * (3)));
                return  turnActivityIntoQuestion(activity, questionType, generateOptions(activityList, 3));
            })
            .collect(Collectors.toList());
    }

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
        List<File> files = getFiles(".json", new File(activitiesPath));
        for (File jsonFile : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
            JSONParser jsonParser = new JSONParser(bufferedReader);
            LinkedHashMap<String, ?> list = (LinkedHashMap<String, ?>) jsonParser.parse();
            String title = list.get("title").toString();
            long wattHours = ((BigInteger) list.get("consumption_in_wh")).longValue();
            String source = list.get("source").toString();

            String str = jsonFile.getName();
            File file = find(activitiesPath, str.substring(0, str.lastIndexOf('.')) + ".png");
            if (file == null) {
                file = find(activitiesPath, str.substring(0, str.lastIndexOf('.')) + ".jpeg");
            }
            if (file == null) {
                file = find(activitiesPath, str.substring(0, str.lastIndexOf('.')) + ".jpg");
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
        // TODO: add the path to the default image
        if (imagePath == null) return new byte[0];
        File file = new File(imagePath);
        String extension = imagePath.substring(imagePath.lastIndexOf('.') + 1);
        try {
            BufferedImage bImage = ImageIO.read(file);
            if (bImage == null) {
                return new byte[0];
            }
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
