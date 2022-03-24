package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Objects;

public class Player {
    @JsonProperty("nick")
    private final String nick;

    @JsonProperty("score")
    private int score;

    //time based on systems default time
    @JsonIgnore
    private Date time = new Date();

    public Player(final String nick) {
        this.nick = nick;
        this.score = 0;
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

    @JsonIgnore
    public Date getTimestamp() {
        return this.time;
    }

    @JsonIgnore
    public void updateTimestamp(final Date newTime) {
        this.time = newTime;
    }

    public void addScore(final int score) {
        this.score += score;
    }

    @JsonIgnore
    public boolean isAlive() {
        Date now = new Date();
        int timerDifference = (int) now.getTime() - (int) this.time.getTime();
        return timerDifference < 5000;
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
