package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("players")
    private List<Player> players;

    @JsonProperty("questions")
    private Question[] questions;

    @JsonProperty("currentQuestion")
    private int currentQuestion;

    public Game(UUID id) {
        this.id = id;
        this.players = new ArrayList<>();
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

    public boolean addPlayer(Player p) {
        if (players.contains(p)) return false;
        players.add(p);
        return true;
    }

    public void nextQuestion() {
        this.currentQuestion++;
    }

    public void start() {
        while (currentQuestion < 20) {
            if (currentQuestion == 10) {
                // Show intermediary leaderboard

                // Sleep 5 seconds
            }
            // Show new question

            // Reset time for all players
            for (Player p : this.players) {
                p.setTime(20000);
            }
            // Start timer for all players

            // Wait until all timers reach 0

            // Show answers

            // Sleep 5 seconds
        }
    }
}
