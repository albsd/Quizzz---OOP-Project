/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.controller;


import commons.Game;
import commons.Leaderboard;
import commons.Player;
import commons.Question;
import commons.JoinMessage;
import commons.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import server.service.ActivityService;
import server.service.GameService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    private final ActivityService activityService;

    @Autowired
    public GameController(final GameService gameService, final ActivityService activityService) {
        this.gameService = gameService;
        this.activityService = activityService;
    }

    /**
     * Fetches all the games that have been played and are to be played.
     *
     * @return List of all Games
     */
    @GetMapping(path = { "", "/" })
    public List<Game> getAll() {
        return gameService.getAll();
    }

    /**
     * Fetches the active game lobby.
     *
     * @return The current active game which accepts new players
     */
    @GetMapping("/current")
    public Game getCurrentGame() {
        return gameService.getCurrentGame();
    }

    /**
     * Fetches the game by its UUID.
     *
     * @param id The UUID of the game
     * @return Game or an error, depending on whether the game exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getById(final @PathVariable("id") UUID id) {
        Game game = gameService.findById(id);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(game);
    }

    @GetMapping("{id}/leaderboard")
    public ResponseEntity<Leaderboard> getLeaderboard(
            @PathVariable final UUID id) {
        if (gameService.findById(id) == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameService.getLeaderboard(id));
    }

    @GetMapping("{id}/question")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable final UUID id) {
        if (gameService.findById(id) == null) {
            return ResponseEntity.badRequest().build();
        }
        System.out.println(gameService.getQuestions(id).get(0).getImageBytes());
        System.out.println(gameService.getQuestions(id).get(1).getImageBytes());
        return ResponseEntity.ok(gameService.getQuestions(id));
    }

    /**
     * Join the active game lobby as a Player with id "nick".
     *
     * @param nick User's nickname which identifies a given player in a game
     * @return Player
     */
    // TODO: add sessionId to path so it can be used to construct the player
    @PostMapping("/join/{nick}")
    public ResponseEntity<Player> joinCurrentGame(final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Game lobby = gameService.getCurrentGame();

        Player p = new Player(nick);
        boolean success = lobby.addPlayer(p);

        final int errorCode = 403; // FORBIDDEN
        if (!success) {
            return ResponseEntity.status(errorCode).build();
        }
        return ResponseEntity.ok(p);
    }

    /**
     * Leave the active game lobby as a Player with id "nick".
     *
     * @param nick User's nickname which identifies a given player in a game
     * @return Player
     */
    // TODO: add sessionId to path so it can be used to find player
    @DeleteMapping("/leave/{nick}")
    public ResponseEntity<Player> leaveCurrentGame(final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Game lobby = gameService.getCurrentGame();
        final int errorCode = 403; // FORBIDDEN

        Player p = lobby.getPlayerByNick(nick);

        if (p == null) {
            return ResponseEntity.status(errorCode).build();
        }
        boolean success = lobby.removePlayer(p);
        System.out.println(success);

        if (!success) {
            return ResponseEntity.status(errorCode).build();
        }
        return ResponseEntity.ok(p);
    }

    // TODO: send generated session id to client so that it can send it back when
    // joining lobby after nickname
    @EventListener
    @SendTo
    private void handleSessionConnected(final SessionConnectEvent event) {
        System.out.println("Client connection");
        System.out.println(event);
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        System.out.println(headers.getSessionId());
    }

    // add leave game code onDisconnect
    @EventListener
    private void handleSessionDisconnect(final SessionDisconnectEvent event) {
        System.out.println("Client disconnected");
        System.out.println(event.getSessionId());
    }

    /*
     * A Websocket endpoint for sending updates about the current lobby status.
     * Namely, updates the active players in the lobby for all clients.
     *
     * @param player The player object who has joined the most recently
     *
     * @return The Player object created from the nick
     */
    @MessageMapping("/join") // /app/join
    @SendTo("/topic/join")
    public JoinMessage joinWebsocket(final JoinMessage joinMessage) {
        return joinMessage;
    }

    @MessageMapping("/lobby/chat") // /app/lobby/chat
    @SendTo("/topic/lobby/chat")
    private Message sendMessage(final Message msg) {
        return msg;
    }

    /**
     * Starts the current game.
     * Do not allow starting a game with less than 2 players.
     *
     * @return The game which has been started
     */
    @PostMapping("/start")
    public ResponseEntity<Game> startCurrentGame() {
        Game lobby = gameService.getCurrentGame();
        if (!lobby.isPlayable()) {
            return ResponseEntity.status(405).build(); // NOT_ALLOWED
        }
        return ResponseEntity.ok(gameService.newGame());
    }
}
