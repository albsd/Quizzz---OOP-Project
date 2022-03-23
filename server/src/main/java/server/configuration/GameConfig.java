package server.configuration;

import commons.Game;
import commons.Player;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import server.controller.AppController;
import server.repository.GameRepository;
import server.service.GameService;

import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class GameConfig {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameService gameService;

    @Autowired
    private AppController appController;


    @Scheduled(fixedRate = 10000)
    public void checkGameAndPlayers() {
        List<Game> games = gameRepo.getGames();
        for (Game game: games) {
            if (game.isOver()) {
                gameRepo.removeGame(game.getId());
            } else {
                List<Player> players = game.getPlayers();
                for (Player player : players) {
                    if (timeDifference(player) > 5000L) {
                        game.removePlayer(player);
                        appController.sendPlayerLeft(game.getId(), player);
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    public void checkLobbyPlayers() {
        Game lobby = gameService.getCurrentGame();
        List<Player> players = lobby.getPlayers();
        for (Player player : players) {
            if (timeDifference(player) > 5000L) {
                lobby.removePlayer(player);
                appController.sendPlayerUpdate(new PlayerUpdate(player.getNick(), PlayerUpdate.Type.leave));
            }
        }
    }

    private long timeDifference(final Player player) {
        Date now = new Date();
        //getTime returns miliseconds
        return Math.abs(now.getTime() - player.getTimestamp().getTime());
    }
}
