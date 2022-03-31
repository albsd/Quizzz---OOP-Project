package server.controller;

import commons.Game;
import commons.GameUpdate;
import commons.Player;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
    public AppController(final GameService gameService, final AppService appService, final SimpMessagingTemplate smt) {
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

        try {
            updateFinishedTimers(id, player.getNick());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the player's state as having finished his timer.
     *
     * @param id id of the game
     * @param nick name of the player
     * @return Game object of the player
     */
    @PostMapping("/{id}/finishedtimer/{nick}")
    public ResponseEntity<Game> updateFinishedTimers(final @PathVariable("id") UUID id,
                                                     final @PathVariable("nick") String nick) {

        Game game = gameService.findById(id);
        if (game == null || game.isOver()) {
            return ResponseEntity.badRequest().build();
        }

        updateGamePlayerFinished(id, nick);

        if (game.allPlayersFinished()) {
            startTimerMessage(id);
        }

        return ResponseEntity.ok(game);
    }

    public Player updateGamePlayerFinished(final UUID id, final String nick) {
        return gameService.updateGamePlayerFinished(id, nick);
    }

    @MessageMapping("/game/{id}/startTimer")
    public void startTimerMessage(final UUID id) {
        smt.convertAndSend("/topic/game/" + id  + "/update", GameUpdate.startTimer);
    }

    /**
     * Endpoint to get nickname of MAC address.
     * @param macAddress unique MAC address of device
     * @return Player object.
     */
    @GetMapping({"/nick/{macAddress}"})
    public ResponseEntity<Player> getNickname(final @PathVariable String macAddress) {
        String nick = appService.getNickname(macAddress);
        if (nick == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new Player(nick));
    }

    /**
     * Endpoint to save nickname with MAC address.
     * @param macAddress unique MAC address of device
     * @param nick nickname of client
     * @return Player object.
     */
    @PostMapping({"/nick/{macAddress}/{nick}"})
    public Player saveNickname(final @PathVariable String macAddress, final @PathVariable String nick) {
        appService.saveNickname(macAddress, nick);
        return new Player(nick);
    }
}
