package commons;

import java.net.URI;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;

public class Question {
    private final String prompt;
    private final byte[] imageBytes;
    private final String[] options;
    private final int answer;

    public Question(String prompt, Path imagePath, String[] options, int answer) {
        byte[] bytes;
        try {
            bytes = readAllBytes(imagePath);
        } catch (Exception e1) {
            try {
                System.err.println("Could not load path '" + imagePath.toString() + "', loading default image instead.");
                URI uri = getClass().getClassLoader().getResource("default.jpg").toURI();
                bytes = readAllBytes(Path.of(uri));
            } catch (Exception e2) {
                System.err.println("Could not load default image, falling back to no image");
                bytes = new byte[0];
            }
        }

        this.prompt = prompt;
        this.imageBytes = bytes;
        this.options = options;
        this.answer = answer;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }

    public String[] getOptions() {
        return this.options;
    }

    public int getAnswer() {
        return this.answer;
    }
}
