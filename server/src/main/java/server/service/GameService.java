package server.service;

import commons.Game;
import commons.Question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.GameRepository;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repo;

    private Game lobby;

    private ServerQuestionTimer serverTimer;

    private UUID randomId;

    @Autowired
    public GameService(final GameRepository repo) {
        this.randomId = UUID.randomUUID();
        this.repo = repo;
        this.lobby = new Game(randomId);
        this.serverTimer = new ServerQuestionTimer(randomId);
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
     * In addition, the game specific server timer is also set
     * 
     * @return Game that has been created
     */
    public Game newGame() {
        // TODO: this method breaks the tests as the while loop is infinite
        // lobby.start();
        repo.addGame(lobby);
        repo.addTimer(serverTimer);
        randomId = UUID.randomUUID();
        lobby = new Game(randomId);
        serverTimer = new ServerQuestionTimer(randomId);
        return lobby;
    }

    public Game findById(final UUID id) {
        return repo.findById(id);
    }

    public ServerQuestionTimer getServerQuestionTimer(final UUID id) {
        return repo.findTimerById(id);
    }

    public Question getMockQuestion(final UUID id) {
        return null;
    }

}
