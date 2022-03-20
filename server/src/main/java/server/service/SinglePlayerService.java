package server.service;
import commons.LeaderboardMessage;
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import server.repository.SinglePlayerRepository;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerService {
    @Autowired
    private SinglePlayerRepository singlePlayerRepository;

    public SinglePlayerService(final SinglePlayerRepository singlePlayerRepository) {
        this.singlePlayerRepository = singlePlayerRepository;
    }

    public void addPlayerToLeaderboard(final LeaderboardMessage singlePlayerLeaderboardMessage) {
        singlePlayerRepository.save(singlePlayerLeaderboardMessage);
    }

    public List<Player> getAllPlayerInfo() {
        List<LeaderboardMessage>allSinglePlayers =  singlePlayerRepository.findAll();
        List<Player> players = new ArrayList<>();
        for(int i = 0; i < allSinglePlayers.size(); i++) {
            LeaderboardMessage sl = allSinglePlayers.get(i);
            players.add(new Player(sl.getNick(), sl.getScore()));
        }
        return players;
    }
}
