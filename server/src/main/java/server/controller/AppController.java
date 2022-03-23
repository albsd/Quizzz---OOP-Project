package server.controller;

import commons.Player;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.GameService;

import java.util.UUID;

@RestController
@RequestMapping("/program")
public class AppController {

    private final GameService gameService;

    @Autowired
    public AppController(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Endpoint to check the lobby players' heartbeat.
     *
     * @param nick name of player
     */
    @GetMapping("/{nick}")
    public Player updateLobbyPlayerTime(final @PathVariable String nick) {
        System.out.println("Received at game controller");
        return gameService.updateLobbyPlayerHeartbeat(nick);
    }

    /**
     * Endpoint to check the game players' heartbeat.
     *
     * @param nick name of player
     * @param id   id of game
     */
    @GetMapping({"/{id}/{nick}"})
    public Player updateGamePlayerTime(final @PathVariable UUID id, final @PathVariable String nick) {
        System.out.println("Received at game controller");
        return gameService.updateGamePlayerHeartbeat(id, nick);
    }
}

//    @SendTo("/topic/game/{id}/leave")
//    public Player sendPlayerLeft(@DestinationVariable final UUID id, final Player player) {
//        return player;
//    }
//
//    @SendTo("/topic/update/player")
//    public PlayerUpdate sendPlayerUpdate(final PlayerUpdate update) {
//        return update;
//    }

    //TODO: check whether server is also active

