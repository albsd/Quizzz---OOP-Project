package server.configuration;

import commons.Game;
import commons.Player;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import server.controller.AppController;
import server.controller.GameController;
import server.repository.GameRepository;
import server.service.GameService;

import java.util.List;

@Configuration
@EnableScheduling
public class GameConfig {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameService gameService;

    @Autowired
    private AppController appCtrl;

    @Autowired
    private GameController gameCtrl;

    @Scheduled(fixedRate = 60000)
    private void checkGamePlayers() {
        List<Game> games = gameRepo.getGames();
        for (Game game : games) {
            List<Player> players = game.getPlayers();
            for (int j = 0; j < players.size(); j++) {
                Player player = players.get(j);
                if (!player.isAlive()) {
                    game.removePlayer(player);
                    appCtrl.sendPlayerLeft(player, game.getId());
                }
            }
            if (players.isEmpty()) {
                gameRepo.removeGame(game.getId());
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    private void checkLobbyPlayers() {
        Game lobby = gameService.getCurrentGame();
        List<Player> players = lobby.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!player.isAlive()) {
                lobby.removePlayer(player);
                appCtrl.sendPlayerUpdate(new PlayerUpdate(player.getNick(), PlayerUpdate.Type.leave));
            }
        }
    }
}
