package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final int questionCount = 20;
    private final int questionTime = 20000;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("players")
    private List<Player> players;

    @JsonProperty("questions")
    private Question[] questions;

    @JsonProperty("currentQuestion")
    private int currentQuestion;
    @JsonProperty("gameState")
    private String gameState = "waiting";

    public Game(final UUID id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.questions = new Question[questionCount];
        // Generating questions is not implemented yet:
        // this.questions = QuestionService.generateQuestions()
        this.currentQuestion = 0;
    }

    public UUID getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Question getCurrentQuestion() {
        return this.questions[this.currentQuestion];
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public boolean addPlayer(Player p) {
        if (players.contains(p))
            return false;
        players.add(p);
        return true;
    }

    public void nextQuestion() {
        this.currentQuestion++;
    }

    public void start() {
        this.gameState = "playing";
        while (currentQuestion < 20) {
            if (currentQuestion == 10) {
                // Show intermediary leaderboard

                // Sleep 5 seconds
                //}
                // Show new question

                // Reset time for all players
                for (Player p : this.players) {
                    p.setTime(questionTime);
                }
                // Start timer for all players

                // Wait until all timers reach 0

                // Show answers

                // Sleep 5 seconds
            }
        }
    }
}
