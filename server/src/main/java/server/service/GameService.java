package server.service;

import commons.Game;
import commons.Question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.GameRepository;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repo;

    @Autowired
    public GameService(final GameRepository repo) {
        this.repo = repo;
    }

    public UUID addGame(final Game game) {
        return repo.addGame(game);
    }

    public boolean deleteGame(final UUID id) {
        return repo.removeGame(id);
    }

    public List<Game> getAll() {
        return repo.getGames();
    }

    public Game findById(final UUID id) {
        return repo.findById(id);
    }

    public ServerQuestionTimer getServerQuestionTimer(final UUID id) {
        return repo.findTimerById(id);
    }

    public Question getMockQuestion() {
        return null;
    }

}
