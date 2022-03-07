package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinMessage {
    @JsonProperty("player")
    private Player player;

    @JsonProperty("joining")
    private boolean joining;

    @JsonCreator
    public JoinMessage(final @JsonProperty("player") Player player,
                       final @JsonProperty("joining") boolean joining) {
        this.player = player;
        this.joining = joining;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public boolean isJoining() {
        return joining;
    }

    public void setJoining(final boolean joining) {
        this.joining = joining;
    }
}
