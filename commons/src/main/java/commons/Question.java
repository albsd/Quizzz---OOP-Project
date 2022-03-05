package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.URI;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;

@Entity
public class Question {

    @Id
    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("imageBytes")
    private byte[] imageBytes;

    @JsonProperty("options")
    private String[] options;

    @JsonProperty("answer")
    private int answer;

    public Question(){

    }

    public Question(final @JsonProperty String prompt,
                    final @JsonProperty Path imagePath,
                    final @JsonProperty String[] options,
                    final @JsonProperty int answer) {
        byte[] bytes;
        try {
            bytes = readAllBytes(imagePath);
        } catch (Exception e1) {
            try {
                System.err.println("Could not load path '"
                                + imagePath.toString()
                                + "', loading default image instead.");
                URI uri = getClass().getClassLoader()
                        .getResource("default.jpg").toURI();
                bytes = readAllBytes(Path.of(uri));
            } catch (Exception e2) {
                System.err.println(
                        "Could not load default image, using no image instead");
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
