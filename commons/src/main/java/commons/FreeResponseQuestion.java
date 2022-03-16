package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class FreeResponseQuestion extends Question {
    @JsonProperty("answer")
    private long answer;

    public FreeResponseQuestion(final String prompt, final byte[] imageBytes, final long answer) {
        super(prompt, imageBytes);
        this.answer = answer;
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
        if (!super.equals(o)) return false;
        FreeResponseQuestion that = (FreeResponseQuestion) o;
        return answer == that.answer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), answer);
    }
}
