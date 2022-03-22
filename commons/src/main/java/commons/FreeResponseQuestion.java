package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FreeResponseQuestion extends Question {

    @JsonCreator
    public FreeResponseQuestion(@JsonProperty("prompt") final String prompt,
                                @JsonProperty("image") final byte[] image,
                                @JsonProperty("answer") final long answer) {
        super(prompt, answer, image);
    }

    public int calculateScore(final long option, final int time) {
        int bonusScore = calculateBonusPoints(time);
        int offPercentage = (int) Math.round(((double) Math.abs((option - getAnswer())) / getAnswer()) * 100);
        int accuracyPercentage = 100 - offPercentage;
        if (accuracyPercentage < 0) {
            accuracyPercentage = 0;
        }
        int base = (accuracyPercentage / 10) * 10;
        return base + bonusScore;
    }
}
