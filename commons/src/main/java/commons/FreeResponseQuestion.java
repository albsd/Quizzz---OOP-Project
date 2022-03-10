package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.util.Objects;

public class FreeResponseQuestion extends Question{
    @JsonProperty("answer")
    private int answer;

    public FreeResponseQuestion(String prompt, Path imagePath, int answer) {
        super(prompt, imagePath);
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FreeResponseQuestion that = (FreeResponseQuestion) o;
        return answer == that.answer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), answer);
    }
}
