package commons;

import java.util.List;

public class Game {
    private List<Player> players;
    private Question[] questions;
    private int currentQuestion;

    public Game(List<Player> players) {
        this.players = players;
        // Generating questions is not implemented yet:
        // this.questions = QuestionService.generateQuestions()
        this.currentQuestion = 0;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Question getCurrentQuestion() {
        return this.questions[this.currentQuestion];
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
