package server.service;

import commons.Game;
import commons.Leaderboard;
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

    @Autowired
    public GameService(final GameRepository repo) {
        this.repo = repo;
    }

    public void initializeLobby(final List<Question> questions) {
        this.lobby = new Game(UUID.randomUUID(), questions, true);
    }

    public Game createSingleplayer(final String nick, final List<Question> questions) {
        return repo.createSingleplayer(nick, questions);
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
     * @param questions List of questions for the newly created lobby
     * @return Game that has been created
     */
    public Game newGame(final List<Question> questions) {
        repo.addGame(lobby);
        lobby = new Game(UUID.randomUUID(), questions, true);
        return lobby;
    }

    public Game findById(final UUID id) {
        return repo.findById(id);
    }

    public Leaderboard getLeaderboard(final UUID id) {
        return repo.getLeaderboard(id);
    }

    public void addPlayerScore(final Game game, final String nick, final int score) {
        repo.addPlayerScore(game, nick, score);
    }
}
