package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class ScoreMessage extends Message<Integer> {

    @JsonProperty("id")
    private final UUID id;

    @JsonCreator
    public ScoreMessage(final @JsonProperty("nick") String nick,
                        final @JsonProperty("score") int score,
                        final @JsonProperty("id") UUID id) {
        super(nick, score);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ScoreMessage that = (ScoreMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
