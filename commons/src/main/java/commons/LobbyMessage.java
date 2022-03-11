package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.Objects;

public class LobbyMessage extends Message<String> {
    @JsonProperty("timestamp")
    private final LocalTime timestamp;

    @JsonCreator
    public LobbyMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("timestamp") LocalTime timestamp,
                       final @JsonProperty("content") String content) {
        super(nick, content);
        this.timestamp = timestamp;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LobbyMessage that = (LobbyMessage) o;
        return timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp);
    }
}
