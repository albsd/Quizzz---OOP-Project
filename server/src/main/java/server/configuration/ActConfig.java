package server.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repository.ActivityRepository;
import server.service.ActivityService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ActConfig {
    @Bean
    CommandLineRunner commandLineRunner(final ActivityRepository actRepo, final ActivityService activityService) {
        return args -> {
//            File file = new File("C:\\Users\\pkcho\\Desktop\\repository-template\\activity.JSON");
//            FileReader fileStream = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileStream);
//            JSONParser jsonParser = new JSONParser(bufferedReader);
//            Object obj = jsonParser.parse();
//            List<LinkedHashMap> list = (ArrayList) obj;
////            for(int i =0; i<list.size(); i++){
////                String title = (String) list.get(i).get("title");
////                String source = (String) list.get(i).get("source");
////                long watt = (Long) list.get(i).get("consumption_in_wh");
////            }
//            System.out.println(list.get(1).get("title"));
//            System.out.println(list.get(1).get("source"));
//            System.out.println(list.get(1).get("consumption_in_wh"));
            List<File> f = getFiles(".json", new File("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities"));
            ObjectMapper mapper = new ObjectMapper();
            for (int i = 0; i < f.size(); i++) {
                Activity activity = mapper.readValue(readFile(f.get(i)), Activity.class);
                activity.setId(i + 1L);
                System.out.println(activity.getTitle());
                if (activity.getSource().length() > 255) {
                    activity.setSource(activity.getSource().substring(0, 255));
                }
                String str = f.get(i).getName();
                System.out.println(str);
                File file = find("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities",
                        str.substring(0, str.lastIndexOf('.')) + ".png");
                if (file == null) {
                    System.out.println(str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                    file = find("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                }
                if (file == null) {
                    System.out.println(str.substring(0, str.lastIndexOf('.')) + ".jpg");
                    file = find("C:\\Users\\pkcho\\Desktop\\activity-bank\\activities",
                            str.substring(0, str.lastIndexOf('.')) + ".jpg");
                }
                System.out.println(file);
                BufferedImage bImage = ImageIO.read(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte[] data = bos.toByteArray();
                activity.setImageBytes(data);
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

    public static String readFile(final File fileToRead) {
        String content = "";
        try (FileReader fileStream = new FileReader(fileToRead);
             BufferedReader bufferedReader = new BufferedReader(fileStream)) {
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                content += line;
            }

        } catch (FileNotFoundException ex) {
            //exception Handling
        } catch (IOException ex) {
            //exception Handling
        }
        return content;
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
