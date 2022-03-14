package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LobbyMessage extends Message<String> {
    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonCreator
    public LobbyMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("timestamp") String timestamp,
                       final @JsonProperty("content") String content) {
        super(nick, content);
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LobbyMessage that = (LobbyMessage) o;
        return timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp);
    }
}
