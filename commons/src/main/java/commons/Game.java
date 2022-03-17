package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

enum GameState { waiting, playing, singleplayer }

public class Game {

    @JsonIgnore
    private QuestionTimer timer;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("players")
    private List<Player> players;

    @JsonProperty("questions")
    private List<Question> questions;

    @JsonProperty("currentQuestion")
    private int currentQuestion;

    @JsonProperty("gameState")
    private GameState gameState;

    public Game(final UUID id, final List<Question> questions) {
        this.id = id;
        this.players = new ArrayList<>();
        this.questions = questions;
        this.currentQuestion = 0;
        this.gameState = GameState.waiting;
    }

    @JsonCreator
    public Game(final @JsonProperty("id") UUID id,
                final @JsonProperty("players") List<Player> players,
                final @JsonProperty("questions") List<Question> questions,
                final @JsonProperty("currentQuestion") int currentQuestion,
                final @JsonProperty("gameState") GameState gameState) {
        this.id = id;
        this.players = players;
        this.questions = questions;
        this.currentQuestion = currentQuestion;
        this.gameState = gameState;
    }

    public UUID getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }
    
    public void setSinglePlayer(final Player p) {
        addPlayer(p);
        gameState = GameState.singleplayer;
    }

    @JsonIgnore
    public boolean showLeaderboard() {
        return gameState == GameState.singleplayer && currentQuestion % 10 == 0;
    }

    @JsonIgnore
    public int getCurrentQuestionNumber() {
        return currentQuestion + 1;
    }

    public QuestionTimer getTimer() {
        return timer;
    }

    /**
     * Since the QuestionTimer cannot be serialised over HTTP, 
     * we can call this method in the client to create a new timer. 
     */
    public void initialiseTimer() {
        this.timer = new QuestionTimer();
    }

    public boolean addPlayer(final Player p) {
        if (players.contains(p)) {
            return false;
        }
        players.add(p);
        return true;
    }

    public boolean removePlayer(final Player p) {
        if (!players.contains(p)) {
            return false;
        }
        players.remove(p);
        return true;
    }

    public Player getPlayerByNick(final String nick) {
        return players.stream()
            .filter((p) -> p.getNick().equals(nick))
            .findFirst()
            .get();
    }

    
    @JsonIgnore
    public int nextQuestion() {
        return currentQuestion++;
    }

    @JsonIgnore
    public int getCurrentQuestionIndex() {
        return currentQuestion;
    }

    @JsonIgnore
    public List<Question> getQuestions() {
        return questions;
    }

    @JsonIgnore
    public Question getCurrentQuestion() {
        return questions.get(currentQuestion);
    }

    @JsonIgnore
    public void start(final Runnable callback) {
        this.gameState = GameState.playing;
        timer.startGameTimer(callback);
    }
}
