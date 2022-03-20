package server.service;
import commons.Leaderboard;
import commons.GameResult;
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.LeaderboardRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {
    @Autowired
    private LeaderboardRepository leaderboardRepository;

    public LeaderboardService(final LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public void addPlayerToLeaderboard(final GameResult singlePlayerGameResult) {
        leaderboardRepository.save(singlePlayerGameResult);
    }

    public Leaderboard getAllPlayerInfo() {
        List<GameResult> allSinglePlayers =  leaderboardRepository.findAll();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < allSinglePlayers.size(); i++) {
            GameResult sl = allSinglePlayers.get(i);
            players.add(new Player(sl.getNick(), sl.getScore()));
        }
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setRanking(players);
        return leaderboard;
    }
}
