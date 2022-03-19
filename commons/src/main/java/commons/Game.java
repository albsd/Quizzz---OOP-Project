package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

enum GameState { waiting, playing }

public class Game {

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
        for (Player p : players) {
            if (p.getNick().equals(nick)) {
                return p;
            }
        }
        return null;
    }

    public void nextQuestion() {
        this.currentQuestion++;
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
}
