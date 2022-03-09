package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LobbyMessage extends Message {
    @JsonProperty("content")
    private String content;

    @JsonCreator
    public LobbyMessage(final @JsonProperty("player") Player player,
                        final @JsonProperty("timestamp") int timestamp,
                       final @JsonProperty("content") String content) {
        super(player, timestamp);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LobbyMessage that = (LobbyMessage) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content);
    }
}
