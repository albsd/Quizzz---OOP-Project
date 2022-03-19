package server.service;

import commons.Activity;
import commons.LeaderboardMessage;
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import server.repository.ActivityRepository;
import server.repository.SinglePlayerRepository;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerService {
    @Autowired
    private SinglePlayerRepository singlePlayerRepository;

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
