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

    public void setScore(final int amount) {
        this.score = amount;
    }

    public void addScore(final int amount) {
        this.score += amount;
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
