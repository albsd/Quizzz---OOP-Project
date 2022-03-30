package server.controller;

import commons.Player;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.service.AppService;
import server.service.GameService;

import java.util.UUID;
/**
 * Controller class to receive and update clients' heartbeats.
 */
@RestController
@RequestMapping("/app")
public class AppController {

    private final GameService gameService;

    private final AppService appService;

    private final SimpMessagingTemplate smt;

    @Autowired
    public AppController(final GameService gameService, final SimpMessagingTemplate smt, final AppService appService) {
        this.gameService = gameService;
        this.smt = smt;
        this.appService = appService;
    }

    /**
     * Endpoint to test whether server is still running.
     *
     * @return true if connected.
     */
    @GetMapping(path = { "", "/" })
    public boolean identity() {
        return true;
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

    /**
     * WS message to send player update in the lobby.
     * @param update status of the player
     */
    public void sendPlayerUpdate(@Payload final PlayerUpdate update) {
        this.smt.convertAndSend("/topic/update/player", update);
    }

    /**
     * WS message to notify players that have left a certain game.
     * @param player object of player that left
     * @param id id of the game
     */
    public void sendPlayerLeft(@Payload final Player player, final UUID id) {
        this.smt.convertAndSend("/topic/game/" + id + "/leave", player);
    }

    /**
     * Endpoint to check the get player's saved nickname.
     */
    @GetMapping({"/{macAddress}"})
    public String getMacAddress(final @PathVariable String macAddress) {
        return appService.getNickname(macAddress);
    }


    /**
     * Endpoint to save nickname with player's MAC address.
     */
    @PostMapping({"/{macAddress}/{nick}"})
    public String saveMacAddress(final @PathVariable String macAddress, @PathVariable String nick) {
        return appService.saveNickname(macAddress, nick);
    }
}
