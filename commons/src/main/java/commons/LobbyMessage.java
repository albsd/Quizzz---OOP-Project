package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LobbyMessage {
    @JsonProperty("nick")
    private String nick;

    @JsonProperty("timestamp")
    private int timestamp;

    @JsonProperty("content")
    private String content;

    @JsonCreator
    public LobbyMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("timestamp") int timestamp,
                       final @JsonProperty("content") String content) {
        this.nick = nick;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getNick() {
        return nick;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyMessage that = (LobbyMessage) o;
        return timestamp == that.timestamp && Objects.equals(nick, that.nick) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, content, timestamp);
    }
}
