package server.service;

import commons.Activity;
import commons.SinglePlayerLeaderboardMessage;
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

    public void addPlayerToLeaderboard(final SinglePlayerLeaderboardMessage singlePlayerLeaderboardMessage) {
        singlePlayerRepository.save(singlePlayerLeaderboardMessage);
    }

    public List<SinglePlayerLeaderboardMessage> getAllPlayerInfo() {
        return singlePlayerRepository.findAll();
    }
}
