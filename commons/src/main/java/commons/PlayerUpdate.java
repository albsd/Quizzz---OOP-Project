package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PlayerUpdate extends Message<PlayerUpdate.Action> {
    public enum Action {
        join, leave
    }

    @JsonProperty("player")
    private Player player;

    @JsonCreator
    public PlayerUpdate(final @JsonProperty("player") Player player,
                        final @JsonProperty("content") Action content) {
        super(content);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlayerUpdate that = (PlayerUpdate) o;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), player);
    }
}
