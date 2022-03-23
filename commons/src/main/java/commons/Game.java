package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<Question> questions;

    @JsonProperty("currentQuestion")
    private int currentQuestion;

    @JsonProperty("isMultiplayer")
    private boolean isMultiplayer;

    public Game(final UUID id, final List<Question> questions, final boolean isMultiplayer) {
        this.id = id;
        this.players = new ArrayList<>();
        this.questions = questions;
        this.currentQuestion = 0;
        this.isMultiplayer = isMultiplayer;
    }

    @JsonCreator
    public Game(final @JsonProperty("id") UUID id,
                final @JsonProperty("players") List<Player> players,
                final @JsonProperty("questions") List<Question> questions,
                final @JsonProperty("currentQuestion") int currentQuestion,
                final @JsonProperty("isMultiplayer") boolean isMultiplayer) {
        this.id = id;
        this.players = players;
        this.questions = questions;
        this.currentQuestion = currentQuestion;
        this.isMultiplayer = isMultiplayer;
    }

    public UUID getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @JsonIgnore
    public boolean isMultiplayer() {
        return this.isMultiplayer;
    }

    @JsonIgnore
    public boolean shouldShowMultiplayerLeaderboard() {
        return (isMultiplayer && getCurrentQuestionNumber() % 10 == 0);
    }

    @JsonIgnore
    public int getCurrentQuestionNumber() {
        return currentQuestion + 1;
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
            .orElse(null);
    }

    @JsonIgnore
    public boolean isOver() {
        return getCurrentQuestionNumber() >= 20;
    }
    
    @JsonIgnore
    public void nextQuestion() {
        currentQuestion++;
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
    public void setCurrentQuestionIndex(final int number) {
        this.currentQuestion = number;
    }
}
