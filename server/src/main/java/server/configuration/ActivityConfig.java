package server.configuration;

import commons.Activity;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repository.ActivityRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//@Configuration
public class ActivityConfig {
//    @Bean
    CommandLineRunner commandLineRunner(final ActivityRepository activityRepository) {
        return args -> {
            List<File> files = getFiles(".json", new File("./server/src/main/resources/activities"));
            System.out.println(files.size());
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
                // ./server/src/main/resources/activities/00/fridge.png
                String path = file.getPath().replaceAll("\\\\", "/");
                String realPath = path.substring(path.lastIndexOf("./server"));
                activityRepository.saveAndFlush(new Activity(title, wattHours, source, realPath));
            }
            System.out.println("Done with db");
        };
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
