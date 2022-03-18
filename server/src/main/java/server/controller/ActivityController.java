package server.controller;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.ActivityService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoint to configure Activity repository.
 * Allow clients to add or remove activities in the repository.
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(final ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Adds the activity in the activity repo in the request body.
     * @param activity activity to be added.
     * @return the activity that was added
     */
    @PostMapping("/add")
    public ResponseEntity<Activity> addActivity(final @RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.addActivity(activity));
    }

    /**
     * removes activity in repo based on id from parameter.
     * @param id Long id
     * @return the activity that was deleted
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Activity> deleteActivity(final @PathVariable Long id) {
        return ResponseEntity.ok(activityService.deleteActivity(id));
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
