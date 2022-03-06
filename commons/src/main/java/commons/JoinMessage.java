package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinMessage {
    @JsonProperty
    private Player player;
    @JsonProperty
    private boolean joining;

    @JsonCreator
    public JoinMessage(final @JsonProperty("player") Player player, final @JsonProperty("joining") boolean joining) {
        this.player = player;
        this.joining = joining;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isJoining() {
        return joining;
    }

    public void setJoining(boolean joining) {
        this.joining = joining;
    }
}
