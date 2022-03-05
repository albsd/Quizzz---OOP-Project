package server.service;

import commons.Game;
import commons.Leaderboard;
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.GameRepository;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repo;

    @Autowired
    public GameService(GameRepository repo) {
        this.repo = repo;
    }

    public UUID addGame(Game game) {
        return repo.addGame(game);
    }

    public boolean deleteGame(UUID id) {
        return repo.removeGame(id);
    }

    public List<Game> getAll() {
        return repo.getGames();
    }

    public Game findById(UUID id) {
        return repo.findById(id);
    }

    public Leaderboard getLeaderboard(UUID id) {
        return repo.getLeaderboard(id);
    }
}
