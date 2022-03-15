package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

//TODO: Make this class abstract?
@Entity
public class Question {

    @Id
    @JsonProperty("prompt")
    private final String prompt;

    @JsonProperty("imageBytes")
    private final byte[] imageBytes;


    @JsonCreator
    public Question(final @JsonProperty("prompt") String prompt,
            final @JsonProperty("imageBytes") byte[] imageBytes) {

        this.prompt = prompt;
        this.imageBytes = imageBytes;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }


    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Question that) {
            return  prompt.equals(that.prompt)
                    && Arrays.equals(imageBytes, that.imageBytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(prompt);
        final int hashInt = 31;
        result = hashInt * result + Arrays.hashCode(imageBytes);
        return result;
    }
}
