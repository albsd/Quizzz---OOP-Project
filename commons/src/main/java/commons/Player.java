package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Player {
    @JsonProperty("nick")
    private final String nick;

    @JsonProperty("score")
    private int score;

    public Player(final String nick) {
        this.nick = nick;
    }

    @JsonCreator
    public Player(final @JsonProperty("nick") String nick,
            final @JsonProperty("score") int score) {
        this.nick = nick;
        this.score = score;
    }

    public String getNick() {
        return this.nick;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public void setScore(final ScoreMessage sm) {
        if (sm.getType().equals("multiple")) {
            this.score = calculateMulChoicePoints(sm.getContent());
        } else {
            this.score = calculateOpenPoints(sm.getAnswer(), sm.getOption(), sm.getContent());
        }
    }

    private int calculateMulChoicePoints(final int time) {
        int base = 50;
        int bonusScore = calculateBonusPoints(time);
        return base + bonusScore;
    }

    private int calculateOpenPoints(final long answer, final long option, final int time) {
        int bonusScore = calculateBonusPoints(time);
        int offPercentage = (int) Math.round(((double) Math.abs((option - answer)) / answer) * 100);
        int accuracyPercentage = 100 - offPercentage;
        if (accuracyPercentage < 0) {
            accuracyPercentage = 0;
        }
        int base = (accuracyPercentage / 10) * 10;
        return base + bonusScore;
    }

    private int calculateBonusPoints(final int time) {
        return (time / 1000) * 2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, score);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof Player that) {
            return nick.equals(that.nick)
                    && score == that.score;
        }
        return false;
    }
}
