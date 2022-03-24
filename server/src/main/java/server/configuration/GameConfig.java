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

import java.util.List;

/**
 * Configuration class that runs scheduled methods.
 * Used to clean up finished or unused game objects
 * in game repository.
 */
@Configuration
@EnableScheduling
public class GameConfig {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameService gameService;

    @Autowired
    private AppController appCtrl;

    /**
     * Every 5 seconds checks the multiplaeyr games is repo.
     * If player list is empty or game is marked over, the game
     * is deleted.
     */
    @Scheduled(fixedRate = 5000)
    private void checkGameAndPlayers() {
        List<Game> games = gameRepo.getGames();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            List<Player> players = game.getPlayers();
            for (int j = 0; j < players.size(); j++) {
                Player player = players.get(j);
                if (!player.isAlive()) {
                    game.removePlayer(player);
                    appCtrl.sendPlayerLeft(player, game.getId());
                }
            }
            if (players.isEmpty() || game.isOver()) {
                gameRepo.removeGame(game.getId());
            }
        }
    }

    /**
     * Every 2 seconds checks whether player in lobby is active.
     * Otherwise, removed and game update transmitted to other
     * players in lobby.
     */
    @Scheduled(fixedRate = 2000)
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
