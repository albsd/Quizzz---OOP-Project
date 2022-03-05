package commons;

import java.util.List;

public class Leaderboard {
    private List<Player> ranking;

    public List<Player> getRanking() {
        return ranking;
    }

    public void setRanking(List<Player> ranking) {
        this.ranking = ranking;
    }
}
