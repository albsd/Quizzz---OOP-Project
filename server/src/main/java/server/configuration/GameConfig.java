package server.configuration;

import commons.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import server.repository.GameRepository;

import java.util.List;

@Configuration
@EnableScheduling
public class GameConfig {

    @Autowired
    private GameRepository gameRepo;


    @Scheduled(fixedRate = 10000)
    public void deleteMarkedGame() {
        List<Game> games = gameRepo.getGames();
        for (Game game: games) {
            if (game.isOver()) {
                gameRepo.removeGame(game.getId());
            }
        }
    }
}
