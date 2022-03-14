package server.service;

import commons.Game;
import commons.Leaderboard;
import commons.Question;
import commons.ScoreMessage;
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

    public UUID addGame(final Game game) {
        return repo.addGame(game);
    }

    public boolean deleteGame(final UUID id) {
        return repo.removeGame(id);
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

    public Leaderboard getLeaderboard(final UUID id) {
        return repo.getLeaderboard(id);
    }

    public List<Question> getQuestions(final long seed) {
        return repo.getQuestions(seed);
    }
    public long generateSeed(final UUID id) {
        return repo.generateSeed(id);
    }

    public void updatePlayerScore(final ScoreMessage scoreMessage) {
        repo.updatePlayerScore(scoreMessage);
    }

}
