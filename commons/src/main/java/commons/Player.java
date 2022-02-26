package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {
    @JsonProperty("nick")
    private final String nick;

    @JsonProperty("time")
    private int time;

    @JsonProperty("score")
    private int score;

    public Player(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return this.nick;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int ms) {
        this.time = ms;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int amount) {
        this.score = amount;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof Player that) {
            return nick.equals(that.nick) && score == that.score && time == that.time;
        }
        return false;
    }
}
