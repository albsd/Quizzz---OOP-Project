package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class MultipleChoiceQuestion extends Question {
//TODO: correctAnswer is convoluted, maybe use an enum here
//0 -> top, 1 -> middle, 2-> bottom
    @JsonProperty("options")
    private String[] options;

    @JsonCreator
    public MultipleChoiceQuestion(@JsonProperty("prompt") final String prompt,
                                  @JsonProperty("imageBytes") final byte[] imageBytes,
                                  @JsonProperty("options") final String[] options,
                                  @JsonProperty("answer") final long answer) {
        super(prompt, answer, imageBytes);
        this.options = options;
    }
    
    public String[] getOptions() {
        return options;
    }

    public void setOptions(final String[] options) {
        this.options = options;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MultipleChoiceQuestion that = (MultipleChoiceQuestion) o;
        return Arrays.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(options);
        return result;
    }
}
