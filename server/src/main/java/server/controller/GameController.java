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
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.service.GameService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(final GameService gameService) {
        this.gameService = gameService;
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
    @Deprecated
    @GetMapping("/{id}")
    public ResponseEntity<Game> getById(final @PathVariable("id") UUID id) {
        Game game = gameService.findById(id);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(game);
    }

    /**
     * Join the active game lobby as a Player with id "nick".
     * 
     * @param nick User's nickname which identifies a given player in a game
     * @return Game to which the user has joined
     */
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

    @PostMapping("/leave/{nick}")
    public ResponseEntity<Player> leaveCurrentGame(final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Game lobby = gameService.getCurrentGame();
        final int errorCode = 403; // FORBIDDEN

        Player p = lobby.getPlayerByNick(nick);

        if(p == null){
            return ResponseEntity.status(errorCode).build();
        }
        boolean success = lobby.removePlayer(p);
        System.out.println(success);

        if (!success) {
            return ResponseEntity.status(errorCode).build();
        }
        return ResponseEntity.ok(p);
    }

    /**
     * A Websocket endpoint for sending updates about the current lobby status.
     * Namely, updates the active players in the lobby for all clients.
     * 
     * @param player The player object who has joined the most recently
     * @return The Player object created from the nick
     */
    @MessageMapping("/join") // /app/join
    @SendTo("/topic/join")
    public Player joinWebsocket(final Player player) {
        return player;
    }

    @MessageMapping("/leave") // /app/leave
    @SendTo("/topic/leave")
    public Player leaveWebsocket(final Player player) {
        return player;
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
