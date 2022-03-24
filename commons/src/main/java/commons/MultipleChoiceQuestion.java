package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class MultipleChoiceQuestion extends Question {

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

    /**
     * Calculates the score of a multiple choice question.
     * Maximum points: 100 (50 for a correct answer and 50 for 100% speed).
     * @param option the option chosen by the player
     * @param time how long it took the player to answer
     * @return the player's points
     */

    public int calculateScore(final long option, final int time) {
        if (option == getAnswer()) {
            int base = 50;
            int bonusScore = calculateBonusPoints(time);
            return base + bonusScore;
        }
        return 0;
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
