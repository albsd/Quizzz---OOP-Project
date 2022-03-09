package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Message {
    @JsonProperty("player")
    private Player player;

    @JsonProperty("timestamp")
    private int timestamp;

    @JsonCreator
    public Message(final @JsonProperty("player") Player player,
                   final @JsonProperty("timestamp") int timestamp) {
        this.player = player;
        this.timestamp = timestamp;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(player, message.player) && Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, timestamp);
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }
}
