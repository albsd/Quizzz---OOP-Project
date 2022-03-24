package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class deals with both the single-player
 * and the multi-player leaderboards
 * and the players are ordered by score.
 */

public class Leaderboard {
    @JsonProperty("ranking")
    private List<Player> ranking;

    public Leaderboard(final @JsonProperty("ranking") List<Player> ranking) {
        this.ranking = ranking.stream().
                sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    public List<Player> getRanking() {
        return ranking;
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
