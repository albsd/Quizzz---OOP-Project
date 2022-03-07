package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    @JsonProperty("ranking")
    private List<Player> ranking = new ArrayList<>();

    public List<Player> getRanking() {
        return ranking;
    }

    public void setRanking(final List<Player> ranking) {
        this.ranking = ranking;
    }
}
