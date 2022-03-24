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
     * If the accuracy is below a specific threshold then no points are gained.
     * To balance the points gained from this type of question,
     * accuracy is taken into account when calculating the bonus score.
     * Example: a player with a 75% accuracy and with a bonus time of 40
     * now gets roughly 88.25 points.
     * Maximum points: 125 (75 for 100% accuracy and 50 for 100% speed & 100% accuracy).
     *
     * @param option value entered by user
     * @param time how long it took the player to answer
     * @return the player's points
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
        
        return (maxScore * accuracyPercentage) / 100 + bonusScore;
    }
}
