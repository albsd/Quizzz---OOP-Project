package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for the open answer questions.
 */
public class FreeResponseQuestion extends Question {

    @JsonCreator
    public FreeResponseQuestion(@JsonProperty("prompt") final String prompt,
                                @JsonProperty("image") final byte[] image,
                                @JsonProperty("answer") final long answer) {
        super(prompt, answer, image);
    }

    /**
     * Calculates the score of an open answer question.
     * Maximum points: 125 (75 for 100% accuracy and 50 for 100% speed).
     *
     * @param option value entered by user
     * @param time how long it took the player to answer
     * @return the score of the player
     */

    public int calculateScore(final long option, final int time) {
        int bonusScore = calculateBonusPoints(time);
        int accuracyPercentage = 100
                - (int) Math.round(((double) Math.abs((option - getAnswer())) / getAnswer()) * 100);
        final int maxScore = 75;

        if (accuracyPercentage < 50) {
            accuracyPercentage = 0;
            bonusScore = 0;
        }

        return (maxScore * accuracyPercentage) / 100 + bonusScore / (1 + ((100 - accuracyPercentage) / 100));
    }
}
