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

import java.util.List;
import java.util.UUID;

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


    @PostMapping("")
    public UUID create() {
        UUID uuid = UUID.randomUUID();
        Game game = new Game(uuid);
        return gameService.addGame(game);
    }

    @GetMapping("{nick}/join")
    public ResponseEntity<Game> decideGameCreationJoin(
            final @PathVariable("nick") String nick) {
        UUID waitingGameId = gameService.getWaitingGameId();
        if (waitingGameId == null) {
            UUID gameId = this.create();
            this.join(gameId, nick);
            //after the client recieves the game id,
            // make sure it sends a websocket connection upgade request
            //this.joinWs(gameId, nick);
            return ResponseEntity.ok(gameService.findById(gameId));
        }
        this.join(waitingGameId, nick);
        //after the client recieves
        // the game id, make sure it sends a websocket connection upgade request
        //this.joinWs(waitingGameId, nick);
        return ResponseEntity.ok(gameService.findById(waitingGameId));
    }

    @MessageMapping("{id}/join")
    @SendTo("/topic/game_join")
    public Player joinWs(final @PathVariable("id") UUID id, final String nick) {
        return join(id, nick).getBody();
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

}
