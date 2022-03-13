    package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;


    public class MultipleChoiceQuestion extends Question {
    //TODO: correctAnswer is convoluted, maybe use an enum here
    //0 -> top, 1 -> middle, 2-> bottom
    @JsonProperty("answer")
    private int correctAnswer;

    @JsonProperty("options")
    private String[] options;

    public MultipleChoiceQuestion(final String prompt, final byte[] imageBytes,
                                  final String[] options, final int answer) {
        super(prompt, imageBytes);
        this.correctAnswer = answer;
        this.options = options;
    }

    public int getAnswer() {
        return correctAnswer;
    }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MultipleChoiceQuestion that = (MultipleChoiceQuestion) o;
            return correctAnswer == that.correctAnswer && Arrays.equals(options, that.options);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(super.hashCode(), correctAnswer);
            result = 31 * result + Arrays.hashCode(options);
            return result;
        }

        public void setAnswer(final int answer) {
        this.correctAnswer = answer;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(final String[] options) {
        this.options = options;
    }
}
