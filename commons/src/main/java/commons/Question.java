package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import static java.nio.file.Files.readAllBytes;

@Entity
public class Question {

    @Id
    @JsonProperty("prompt")
    private final String prompt;

    @JsonProperty("imageBytes")
    private final byte[] imageBytes;

    @JsonProperty("answer")
    private final int answer;

    @JsonCreator
    public Question(final @JsonProperty("prompt") String prompt,
            final @JsonProperty("imagePath") Path imagePath,
            final @JsonProperty("answer") int answer) {
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
        this.answer = answer;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }

    public int getAnswer() {
        return this.answer;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Question that) {
            return answer == that.answer
                    && prompt.equals(that.prompt)
                    && Arrays.equals(imageBytes, that.imageBytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(prompt, answer);
        final int hashInt = 31;
        result = hashInt * result + Arrays.hashCode(imageBytes);
        return result;
    }
}
