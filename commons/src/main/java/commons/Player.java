package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Player {
    @JsonProperty("nick")
    private final String nick;

    @JsonProperty("score")
    private int score;

    @JsonProperty("time")
    private String timestamp;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss");

    public Player(final String nick) {
        this.nick = nick;
        this.score = 0;
        this.timestamp = ZonedDateTime.now().format(timeFormat);
    }

    @JsonCreator
    public Player(final @JsonProperty("nick") String nick,
            final @JsonProperty("score") int score) {
        this.nick = nick;
        this.score = score;
        this.timestamp = ZonedDateTime.now().format(timeFormat);
    }

    public String getNick() {
        return this.nick;
    }

    public int getScore() {
        return this.score;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void updateTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public void addScore(final int score) {
        this.score += score;
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
