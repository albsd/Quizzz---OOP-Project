package server.service;
import commons.Game;
import commons.Leaderboard;
import commons.Player;
import commons.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.GameRepository;

import java.util.Date;
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

    public Game getSingleGame(final List<Question> questions) {
        return repo.getSingleGame(questions);
    }

    public Game getLobby() {
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
     */
    public void upgradeLobby(final List<Question> questions) {
        repo.addGame(lobby);
        lobby = new Game(UUID.randomUUID(), questions, true);
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

    public Player updateLobbyPlayerHeartbeat(final String nick) {
        Player player = lobby.getPlayerByNick(nick);
        
        // Can occur when the scheduled task executes after the game was started
        if (player == null) return null;
        
        player.updateTimestamp(new Date());
        return player;
    }

    public Player updateGamePlayerHeartbeat(final UUID id, final String nick) {
        Player player = repo.findById(id).getPlayerByNick(nick);
        player.updateTimestamp(new Date());
        return player;
    }

    public Player updateGamePlayerFinished(final UUID id, final String nick) {
        Player player = repo.findById(id).getPlayerByNick(nick);
        player.setFinishedQuestion(true);
        return player;
    }

    public Game setGameOver(final UUID id) {
        Game game = repo.findById(id);
        game.setCurrentQuestionIndex(19);
        return game;
    }
}
