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
    private AppController appCtrl;

    @Autowired
    private GameController gameCtrl;


    @Scheduled(fixedRate = 5000)
    private void checkGameAndPlayers() {
        List<Game> games = gameRepo.getGames();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (game.isOver()) {
                gameRepo.removeGame(game.getId());
                System.out.println("Game finished, removed it");
            } else {
                List<Player> players = game.getPlayers();
                if (players.isEmpty()) {
                    gameRepo.removeGame(game.getId());
                    System.out.println("Removed game");
                } else {
                    for (int j = 0; j < players.size(); j++) {
                        Player player = players.get(j);
                        if (removePlayer(player, game)) {
                            appCtrl.sendPlayerLeft(player, game.getId());
                        }
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    private void checkLobbyPlayers() {
        Game lobby = gameService.getCurrentGame();
        List<Player> players = lobby.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (removePlayer(player, lobby)) {
                appCtrl.sendPlayerUpdate(new PlayerUpdate(player.getNick(), PlayerUpdate.Type.leave));
            }
        }
    }
//don't use while loops
    private boolean removePlayer(final Player player, final Game game) {
        //getTime returns miliseconds
        Date now = new Date();
        long timerDifference = Math.abs(now.getTime() - player.getTimestamp().getTime());
        if (timerDifference > 5000L) {
            game.removePlayer(player);
            return true;
        }
        return false;
    }
}
