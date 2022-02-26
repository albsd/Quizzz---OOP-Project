package client.utils;

import java.util.List;
import java.util.UUID;

public class DummyGame {
    private UUID id;
    private List<DummyPlayer> players;

    public List<DummyPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<DummyPlayer> players) {
        this.players = players;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
