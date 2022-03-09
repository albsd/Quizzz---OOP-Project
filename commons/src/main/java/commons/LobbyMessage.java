package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LobbyMessage extends Message<String> {
    @JsonProperty("nick")
    private String nick;

    @JsonProperty("timestamp")
    private int timestamp;

    @JsonCreator
    public LobbyMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("timestamp") int timestamp,
                       final @JsonProperty("content") String content) {
        super(content);
        this.nick = nick;
        this.timestamp = timestamp;
    }

    public String getNick() {
        return nick;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LobbyMessage that = (LobbyMessage) o;
        return timestamp == that.timestamp && Objects.equals(nick, that.nick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nick, timestamp);
    }
}
