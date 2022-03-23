package server.controller;

import commons.Player;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate smt;

    @Autowired
    public AppController(final GameService gameService, final SimpMessagingTemplate smt) {
        this.gameService = gameService;
        this.smt = smt;
    }

    /**
     * Endpoint to check the lobby players' heartbeat.
     *
     * @param nick name of player
     * @return player
     */
    @GetMapping("/{nick}")
    public Player updateLobbyPlayerTime(final @PathVariable String nick) {
        return gameService.updateLobbyPlayerHeartbeat(nick);
    }

    /**
     * Endpoint to check the game players' heartbeat.
     *
     * @param id   id of game
     * @param nick name of player
     * @return player
     */
    @GetMapping({"/{id}/{nick}"})
    public Player updateGamePlayerTime(final @PathVariable UUID id, final @PathVariable String nick) {
        return gameService.updateGamePlayerHeartbeat(id, nick);
    }

    public void sendPlayerUpdate(@Payload final PlayerUpdate update) {
        this.smt.convertAndSend("/topic/update/player", update);
    }

    public void sendPlayerLeft(@Payload final Player player, final UUID id) {
        this.smt.convertAndSend("/topic/game/" + id + "/leave", player);
        System.out.println("sent update");
    }
}

    //TODO: check whether server is also active

