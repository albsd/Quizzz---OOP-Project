package server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("players")
    private List<Player> players;

    public UUID getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Game(UUID id) {
        this.id = id;
        this.players = new ArrayList<>();
    }

    public boolean addPlayer(Player p) {
        if (players.contains(p)) return false;
        players.add(p);
        return true;
    }
}
