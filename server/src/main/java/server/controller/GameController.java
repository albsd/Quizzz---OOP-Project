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
import commons.LobbyMessage;
import commons.GameUpdate;
import commons.EmoteMessage;
import commons.GameResult;
import commons.PlayerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import server.service.ActivityService;
import server.service.GameService;
import server.service.LeaderboardService;

import java.util.UUID;

/**
 * Controller class for handling lobby, players, game, score, 
 * leaderboard, and chat related requests and WS messages.
 */
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    private final ActivityService activityService;

    private final LeaderboardService leaderboardService;
    
    private final SimpMessagingTemplate smt;

    @Autowired
    public GameController(final GameService gameService, final ActivityService activityService,
                          final LeaderboardService leaderboardService,  final SimpMessagingTemplate smt) {
        this.gameService = gameService;
        this.activityService = activityService;
        this.leaderboardService = leaderboardService;
        this.smt = smt;
        gameService.initializeLobby(activityService.getQuestionList());

        UUID defaultID = UUID.randomUUID();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Game game = new Game(defaultID, activityService.getQuestionList(), false);
                    gameService.addSingleGame(game);
                }
            }
        });
        t1.start();
    }

    // SINGLEPLAYER GAME ENDPOINTS ====================================================================================
    /**
     * Gets the leaderboard for singleplayer client.
     * 
     * @return leaderboard object
     */
    @GetMapping("/leaderboard")
    public Leaderboard getSinglePlayerLeaderboard() {
        return leaderboardService.getAllPlayerInfo();
    }

    /**
     * Updates a client's score and nickname on server database.
     * 
     * @param nick name of player
     * @param score score from latest singleplayer game
     * @return leaderboard object
     */
    @PostMapping("/leaderboard/{nick}/{score}")
    public ResponseEntity<Leaderboard> updateSinglePlayerLeaderboard(final @PathVariable("nick") String nick,
                                                                     final @PathVariable("score") int score) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        GameResult gameResult = new GameResult(nick, score);
        leaderboardService.addPlayerToLeaderboard(gameResult);
        return ResponseEntity.ok(leaderboardService.getAllPlayerInfo());
    }

    /**
     * Creates a game for a singleplayer game. This returns a prebuilt
     * single player game and on another thread, creates and adds another
     * game to replace that one.
     *
     * @param nick Name of the player who started a singleplayer game
     * @return Game object for singleplayer
     */
    @PostMapping("/single/{nick}")
    public Game createAndGetGame(final @PathVariable String nick) {
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                gameService.createSingleplayer(activityService.getQuestionList());
            } 
        });
        t2.start();
        Game game = gameService.getSingleGame();
        game.addPlayer(new Player(nick));
        return game;
    }
    
    // MULTIPLAYER GAME ENDPOINTS =====================================================================================
    // REQUESTS FOR THE LOBBY -----------------------------------------------------------------------------------------
    /**
     * Fetches the active game lobby.
     *
     * @return The current active game which accepts new players
     */
    @GetMapping("/lobby")
    public Game getLobby() {
        return gameService.getLobby();
    }
    
    /**
     * Join the active game lobby as a Player with id "nick".
     *
     * @param nick User's nickname which identifies a given player in a game
     * @return Player
     */
    @PostMapping("/join/{nick}")
    public ResponseEntity<Player> joinLobby(final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Game lobby = gameService.getLobby();

        Player p = new Player(nick);
        boolean success = lobby.addPlayer(p);

        final int errorCode = 403; // FORBIDDEN
        if (!success) {
            return ResponseEntity.status(errorCode).build();
        }

        smt.convertAndSend("/topic/update/player", new PlayerUpdate(p.getNick(), PlayerUpdate.Type.join));
        return ResponseEntity.ok(p);
    }

    /**
     * Leave the active game lobby as a Player with id "nick".
     *
     * @param nick User's nickname which identifies a given player in a game
     * @return Player
     */
    @DeleteMapping("/leave/{nick}")
    public ResponseEntity<Player> leaveLobby(final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Game lobby = gameService.getLobby();
        final int errorCode = 403; // FORBIDDEN

        Player p = lobby.getPlayerByNick(nick);

        if (p == null) {
            return ResponseEntity.status(errorCode).build();
        }
        boolean success = lobby.removePlayer(p);

        if (!success) {
            return ResponseEntity.status(errorCode).build();
        }

        smt.convertAndSend("/topic/update/player", new PlayerUpdate(p.getNick(), PlayerUpdate.Type.leave));
        return ResponseEntity.ok(p);
    }

    // REQUESTS FOR AN ARBITRARY GAME WITH ID -------------------------------------------------------------------------
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
    
    /**
     * Marks the game as finished which will be deleted later in the
     * scheduled task of server.
     * 
     * @param id the id of game
     * @return Game object that was marked finished
     */
    @PostMapping("/{id}")
    public Game setGameOver(final @PathVariable("id") UUID id) {
        return gameService.setGameOver(id);
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<Leaderboard> getLeaderboard(@PathVariable final UUID id) {
        if (gameService.findById(id) == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(gameService.getLeaderboard(id));
    }
    
    /**
     * Leave the active game as a Player with id "nick".
     *
     * @param nick User's nickname which identifies a given player in a game
     * @param id   UUID of the game that the player has left
     * @return Player
     */
    @DeleteMapping("/{id}/player/{nick}")
    public ResponseEntity<Player> leaveGame(final @PathVariable UUID id, final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Game game = gameService.findById(id);
        final int errorCode = 403; // FORBIDDEN

        Player p = game.getPlayerByNick(nick);
        boolean success = game.removePlayer(p);

        if (!success) {
            return ResponseEntity.status(errorCode).build();
        }

        smt.convertAndSend("/topic/game/" + id  + "/leave", p);
        return ResponseEntity.ok(p);
    }

    /**
     * A Websocket endpoint for sending updates about the game's player' status.
     * Namely, updates the active players in the game for all clients.
     *
     * @param update The object containing a player who has joined/left
     * @return The PlayerUpdate object
     */
    @MessageMapping("/update/player") // /app/update/player
    @SendTo("/topic/update/player")
    private PlayerUpdate sendPlayerUpdate(final PlayerUpdate update) {
        return update;
    }

    /**
     * WS endpoint to start the game for multiplayers.
     * 
     * @return GameUpdate to start the game
     */
    @MessageMapping("/lobby/start") // /app/lobby/start
    @SendTo("/topic/lobby/start")
    public GameUpdate startLobby() {
        gameService.addLobby();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                gameService.newGame(activityService.getQuestionList());
            }
        });
        t1.start();
        return GameUpdate.start;
    }

    /**
     * Updates the multiplayers' scores on game repo for specific games.
     * 
     * @param id id of game
     * @param nick name of player
     * @param score score received for the latest question
     * @return Game object of the player
     */
    @PostMapping("/{id}/score/{nick}")
    public ResponseEntity<Game> addPlayerPoints(final @PathVariable UUID id,
            final @PathVariable String nick, final @RequestBody String score) {
        Game game = gameService.findById(id);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }
        gameService.addPlayerScore(game, nick, Integer.parseInt(score));
        return ResponseEntity.ok(game);
    }

    // WEBSOCKET ENDPOINTS FOR THE MULTIPLAYER ------------------------------------------------------------------------
    /**
     * A Websocket endpoint for sending chat messages in the lobby.
     *
     * @param message The message to be sent to all the players in the lobby
     * @return The LobbyMessage object
     */
    @MessageMapping("/lobby/chat") // /app/lobby/chat
    @SendTo("/topic/lobby/chat")
    private LobbyMessage sendLobbyMessage(final LobbyMessage message) {
        return message;
    }

    /**
     * Send an emote message to the game with the given id.
     * 
     * @param message EmoteMessage to be sent
     * @return The same message object
     */
    @MessageMapping("/game/{id}/chat") // /app/game/cc0b8204-8d8c-40bb-a72a-b82f583260c8/chat
    @SendTo("/topic/game/{id}/chat")
    private EmoteMessage sendEmoteMessage(final EmoteMessage message) {
        return message;
    }

    /**
     * A Websocket endpoint for halving the time for other users.
     *
     * @return The GameUpdate.halveTimer enum property
     */
    @MessageMapping("/game/{id}/halve") // /app/game/cc0b8204-8d8c-40bb-a72a-b82f583260c8/halve
    @SendTo("/topic/game/{id}/update")
    public GameUpdate halveTimeWebsocket() {
        return GameUpdate.halveTimer;
    }

}
