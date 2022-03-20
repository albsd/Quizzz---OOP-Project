package server.configuration;

import commons.Activity;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repository.ActivityRepository;
import server.service.ActivityService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


//temporary file to push new activity records into database
@Configuration
public class ActConfig {
    @Bean
    CommandLineRunner commandLineRunner(final ActivityService activityService, final ActivityRepository activityRepository) {
        return args -> {
//            Activity activity = activityRepository.findTopByOrderByIdDesc().get();
//            System.out.println(activity.getId());
//            System.out.println(activity.getTitle());
//            System.out.println(activity.getSource());
//            System.out.println(Arrays.toString(activity.getImageBytes()));

            List<File> f = getFiles(".json", new File("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities"));
            for (int i = 0; i < 2; i++) {
                File fl = f.get(i);
                FileReader fileStream = new FileReader(fl);
                BufferedReader bufferedReader = new BufferedReader(fileStream);
                JSONParser jsonParser = new JSONParser(bufferedReader);
                Object obj = jsonParser.parse();
                LinkedHashMap list = (LinkedHashMap) obj;
                String str = fl.getName();
                System.out.println(str);
                String extension = "png";
                File file = find("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities",
                        str.substring(0, str.lastIndexOf('.')) + ".png");
                if (file == null) {
                    System.out.println(str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                    file = find("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                    extension = "jpeg";
                }
                if (file == null) {
                    System.out.println(str.substring(0, str.lastIndexOf('.')) + ".jpg");
                    file = find("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpg");
                    extension = "jpg";
                }
                String title = list.get("title").toString();
                BigInteger bigvolt = (BigInteger) list.get("consumption_in_wh");
                long volt = bigvolt.longValue();
                String source = list.get("source").toString();

                //check if accurate
                String path = getClass().getClassLoader().getResource(title + "." + extension).getPath();
                System.out.println(path);

                Activity activity = new Activity(title, volt, source, path);
                activity.setId(i + 1L);
                activityService.addActivity(activity);
            }
        };
    }
    public static File find(final String path, final String fName) {
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

    public static List<File> getFiles(final String ext, final File folder) {
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
