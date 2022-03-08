package server.service;

import commons.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.GameRepository;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repo;

    private Game lobby;

    @Autowired
    public GameService(final GameRepository repo) {
        this.repo = repo;
        this.lobby = new Game(UUID.randomUUID());
    }

    public Game getCurrentGame() {
        return lobby;
    }

    public List<Game> getAll() {
        return repo.getGames();
    }

    /**
     * Creates a new game as an active lobby.
     * The previous lobby is propagated to the game that has just started.
     * 
     * @return Game that has been created
     */
    public Game newGame() {
        // TODO: this method breaks the tests as the while loop is infinite
        // lobby.start();
        repo.addGame(lobby);
        lobby = new Game(UUID.randomUUID());
        return lobby;
    }

    public Game findById(final UUID id) {
        return repo.findById(id);
    }
}
