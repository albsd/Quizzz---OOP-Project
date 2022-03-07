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
import commons.Message;
import commons.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(path = { "", "/" })
    public List<Game> getAll() {
        return gameService.getAll();
    }

    @Deprecated
    @PostMapping("")
    public UUID create() {
        UUID uuid = UUID.randomUUID();
        Game game = new Game(uuid);
        return gameService.addGame(game);
    }

    @MessageMapping("{id}/join")
    @SendTo("/topic/game_join")
    public Player joinWs(final @PathVariable("id") UUID id, final String nick) {
        return join(id, nick).getBody();
    }

    // path is /app/lobby/chat
    @MessageMapping("/lobby/chat")
    @SendTo("/topic/lobby/chat")
    private static Message sendMessage(final Message msg) throws InterruptedException {
        return msg;
    }

    @PostMapping("{id}/{nick}")
    public ResponseEntity<Player> join(
            final @PathVariable("id") UUID id,
            final @PathVariable("nick") String nick) {
        if (nick == null || nick.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Game game = gameService.findById(id);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        Player p = new Player(nick);
        boolean success = game.addPlayer(p);

        final int errorCode = 403;
        if (!success) return ResponseEntity.status(errorCode).build();
        return ResponseEntity.ok(p);
    }

    @GetMapping("{id}")
    public ResponseEntity<Game> getById(final @PathVariable("id") UUID id) {
        Game game = gameService.findById(id);
        if (game == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(game);
    }

    @GetMapping("{id}/leaderboard")
    public ResponseEntity<Leaderboard> getLeaderboard(@PathVariable UUID id){
        if (gameService.findById(id) == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(gameService.getLeaderboard(id));
    }
}
