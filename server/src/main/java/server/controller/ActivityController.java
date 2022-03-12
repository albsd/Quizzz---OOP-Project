package server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.ActivityService;
import server.service.GameService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/activity")
/*
   Do not need to use these methods unless we are not allowed to store the quizzz.mv.db file on the repo
   , in which case you would replace the path to your path and make a POST request to /activity/addAllAct
 */
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(final GameService gameService, final ActivityService activityService) {
        this.activityService = activityService;
    }
    //get all activities
    @GetMapping("/getAct")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    //add all activities
    @PostMapping("/addAllAct")
    public void addAllActivities() throws IOException {
        //parse json into activity
        List<File> f = getFiles(".json", new File("C:\\Users\\LohithSai\\Desktop\\activity-bank\\activities"));
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < f.size(); i++) {
            Activity a = mapper.readValue(readFile(f.get(i)), Activity.class);
            System.out.println(a.getTitle());
            if (a.getSource().length() > 255) {
                a.setSource(a.getSource().substring(0, 255));
            }
            String str = f.get(i).getName();
            System.out.println(str);
            File file = find("C:\\Users\\LohithSai\\Desktop\\activity-bank\\activities",
                    str.substring(0, str.lastIndexOf('.')) + ".png");
            if (file == null) {
                System.out.println(str.substring(0, str.lastIndexOf('.')) + ".jpeg");
                file = find("C:\\Users\\LohithSai\\Desktop\\activity-bank\\activities",
                        str.substring(0, str.lastIndexOf('.')) + ".jpeg");
            }
            if (file == null) {
                System.out.println(str.substring(0, str.lastIndexOf('.')) + ".jpg");
                file = find("C:\\Users\\LohithSai\\Desktop\\activity-bank\\activities",
                        str.substring(0, str.lastIndexOf('.')) + ".jpg");
            }
            System.out.println(file);
            BufferedImage bImage = ImageIO.read(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] data = bos.toByteArray();
            a.setImageBytes(data);
            activityService.addActivity(a);
        }
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
