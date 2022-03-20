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
//@Configuration
public class ActConfig {
//    @Bean
    CommandLineRunner commandLineRunner(final ActivityService activityService,
                                        final ActivityRepository activityRepository) {
        return args -> {
            //change path to relative
            List<File> f = getFiles(".json", new File("C:/Users/pkcho/Desktop/repository-template/server/src/main/resources/activities"));
            for (int i = 0; i < f.size(); i++) {
                File fl = f.get(i);
                FileReader fileStream = new FileReader(fl);
                BufferedReader bufferedReader = new BufferedReader(fileStream);
                JSONParser jsonParser = new JSONParser(bufferedReader);
                Object obj = jsonParser.parse();
                LinkedHashMap list = (LinkedHashMap) obj;
                String str = fl.getName();
                File file = find("C:/Users/pkcho/Desktop/repository-template/server/src/main/resources/activities",
                        str.substring(0, str.lastIndexOf('.')) + ".png");
                if (file == null) {
                    file = find("C:/Users/pkcho/Desktop/repository-template/server/src/main/resources/activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                }
                if (file == null) {
                    file = find("C:/Users/pkcho/Desktop/repository-template/server/src/main/resources/activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpg");
                }
                String title = list.get("title").toString();
                long wattHours = ((BigInteger) list.get("consumption_in_wh")).longValue();
                String source = list.get("source").toString();

                //example:
                // /resources/activities/00/fridge.png
                String path = file.getPath().replaceAll("\\\\", "/");
                String realPath = path.substring(path.lastIndexOf("/resources"));
                activityService.addActivity(new Activity(title, wattHours, source, realPath));
//            Activity activity = activityRepository.findTopByOrderByIdDesc().get();
//            System.out.println(activity.getId());
//            System.out.println(activity.getTitle());
//            System.out.println(activity.getSource());
//            System.out.println(Arrays.toString(activity.getImageBytes()));
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
