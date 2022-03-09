package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Leaderboard {
    @JsonProperty("ranking")
    private List<Player> ranking = new ArrayList<>();

    public List<Player> getRanking() {
        return ranking;
    }

    public void setRanking(final List<Player> ranking) {
        this.ranking = ranking;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranking);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof Leaderboard that) {
            return ranking.containsAll(that.getRanking()) && that.getRanking().containsAll(ranking);
        }
        return false;
    }
}
