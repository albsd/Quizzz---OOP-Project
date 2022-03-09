package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class JoinMessage extends Message {
    @JsonProperty("joining")
    private boolean joining;

    @JsonCreator
    public JoinMessage(final @JsonProperty("player") Player player,
                        final @JsonProperty("joining") boolean joining) {
        super(player, 0);
        this.joining = joining;
    }

    public boolean isJoining() {
        return joining;
    }

    public void setJoining(final boolean joining) {
        this.joining = joining;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JoinMessage that = (JoinMessage) o;
        return joining == that.joining;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), joining);
    }
}
