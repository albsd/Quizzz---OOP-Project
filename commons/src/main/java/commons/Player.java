package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Player {
    @JsonProperty("nick_name")
    private String nick;

    @JsonProperty("time")
    private int time;

    @JsonProperty("score")
    private int score;
    private Player() {

    }
    public Player(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return this.nick;
    }

    public int getTime() {
        return this.time;
    }

    public int getScore() {
        return this.score;
    }

    public void setTime(final int ms) {
        this.time = ms;
    }

    public void setScore(final int amount) {
        this.score = amount;
    }

    public void addScore(final int amount) {
        this.score += amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, time, score);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other instanceof Player that) {
            return nick.equals(that.nick)
                    && score == that.score
                    && time == that.time;
        }
        return false;
    }
}
