package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

enum GameState { waiting, playing }

public class Game {
    private final int questionLimit = 20;

    @JsonIgnore
    private QuestionTimer timer;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("players")
    private List<Player> players;

    @JsonProperty("questions")
    private Question[] questions;

    @JsonProperty("currentQuestion")
    private int currentQuestion;

    @JsonProperty("gameState")
    private GameState gameState;

    public Game(final UUID id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.questions = new Question[questionLimit];
        // Generating questions is not implemented yet:
        // this.questions = QuestionService.generateQuestions()
        this.currentQuestion = 0;
        this.gameState = GameState.waiting;
        this.timer = new QuestionTimer(id);
    }

    @JsonCreator
    public Game(final @JsonProperty("id") UUID id,
                final @JsonProperty("players") List<Player> players,
                final @JsonProperty("questions") Question[] questions,
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
        return this.gameState;
    }

    @JsonIgnore
    //Todo: invoke this method when the client-timer is 0 in a set interval
    public void startTimer(final Runnable callback) {
        if (timer.isOver()) {
            timer.reset();
        }
        timer.startGameTimer(callback);
    }

    public Question getCurrentQuestion() {
        return this.questions[this.currentQuestion];
    }

    @JsonIgnore
    public boolean isPlayable() {
        return players.size() >= 2;
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
    //if not ignored, game in serverUtil from getplayers is null
    @JsonIgnore
    public int getQuestionNumber() {
        return this.currentQuestion;
    }
    @JsonIgnore
    public void start(final Runnable callback) {
        this.gameState = GameState.playing;
        startTimer(callback);
    }
}
