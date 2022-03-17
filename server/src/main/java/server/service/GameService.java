package server.service;

import commons.Game;
import commons.Leaderboard;
import commons.Question;
import commons.ScoreMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.GameRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repo;

    private Game lobby;

    private static HashMap<UUID, List<Question>> questionsPerGame = new HashMap<>();

    private ActivityService activityService;

    @Autowired
    public GameService(final GameRepository repo, final ActivityService activityService) {
        this.repo = repo;
        this.lobby = new Game(UUID.randomUUID(), activityService.getQuestionList().toArray(new Question[0]));
        this.activityService = activityService;
    }

    public UUID addGame(final Game game) {
        return repo.addGame(game);
    }

    public boolean deleteGame(final UUID id) {
        return repo.removeGame(id);
    }

    public Game getCurrentGame() {
        System.out.println(lobby.getCurrentQuestion().getPrompt());
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
        lobby = new Game(UUID.randomUUID(), activityService.getQuestionList().toArray(new Question[0]));
        return lobby;
    }

    public Game findById(final UUID id) {
        return repo.findById(id);
    }

    public Leaderboard getLeaderboard(final UUID id) {
        return repo.getLeaderboard(id);
    }

    public List<Question> getQuestions(final UUID id) {
        if (questionsPerGame.containsKey(id)) {
            return questionsPerGame.get(id);
        } 
        
        questionsPerGame.put(id, activityService.getQuestionList());
        return questionsPerGame.get(id);
    }

    public void updatePlayerScore(final Game game, final ScoreMessage scoreMessage) {
        repo.updatePlayerScore(game, scoreMessage);
    }

}
