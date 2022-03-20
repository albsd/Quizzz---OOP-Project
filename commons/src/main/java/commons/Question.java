package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Objects;

@Entity
public abstract class Question {
    @Id
    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("answer")
    private long answer;

    @JsonProperty("image")
    private byte[] image;

    @JsonCreator
    public Question(final @JsonProperty("prompt") String prompt,
                    final @JsonProperty("answer") long answer,
                    final @JsonProperty("image") byte[] image) {
        this.prompt = prompt;
        this.answer = answer;
        this.image = image;
    }

    public Question() {
    }

    public String getPrompt() {
        return this.prompt;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(final long answer) {
        this.answer = answer;
    }

    public byte[] getImage() {
        return image;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return answer == question.answer && Objects.equals(prompt, question.prompt)
                && Arrays.equals(image, question.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(prompt, answer);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
