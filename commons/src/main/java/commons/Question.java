package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public abstract class Question {
    @Id
    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("answer")
    private long answer;

    @JsonProperty("image_path")
    private String path;

    @JsonCreator
    public Question(final @JsonProperty("prompt") String prompt,
                    final @JsonProperty("answer") long answer,
                    final @JsonProperty("image_path") String path) {
        this.prompt = prompt;
        this.answer = answer;
        this.path = path;
    }

    public Question() {
    }

    public String getPrompt() {
        return this.prompt;
    }

    public String getPath() {
        return this.path;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(final long answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return answer == question.answer && Objects.equals(prompt, question.prompt)
                && Objects.equals(path, question.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prompt, answer, path);
    }
}
