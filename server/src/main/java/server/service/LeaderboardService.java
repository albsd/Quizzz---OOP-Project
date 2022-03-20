package server.service;
import commons.Leaderboard;
import commons.LeaderboardMessage;
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.SinglePlayerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {
    @Autowired
    private SinglePlayerRepository singlePlayerRepository;

    public LeaderboardService(final SinglePlayerRepository singlePlayerRepository) {
        this.singlePlayerRepository = singlePlayerRepository;
    }

    public void addPlayerToLeaderboard(final LeaderboardMessage singlePlayerLeaderboardMessage) {
        singlePlayerRepository.save(singlePlayerLeaderboardMessage);
    }

    public Leaderboard getAllPlayerInfo() {
        List<LeaderboardMessage> allSinglePlayers =  singlePlayerRepository.findAll();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < allSinglePlayers.size(); i++) {
            LeaderboardMessage sl = allSinglePlayers.get(i);
            players.add(new Player(sl.getNick(), sl.getScore()));
        }
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setRanking(players);
        return leaderboard;
    }
}
