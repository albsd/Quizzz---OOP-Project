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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import commons.Game;
import commons.Player;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.repository.GameRepository;
import server.service.GameService;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GameControllerTest {

    private GameService service;

    private GameController ctrl;

    private Game lobby;

    private Game game;

    @BeforeEach
    public void setup() {
        service = new GameService(new GameRepository());
        ctrl = new GameController(service);
        // The current lobby is promoted to a game
        // a new lobby is returned after promotion
        game = ctrl.getCurrentGame().getBody();
        ctrl.joinCurrentGame("johny");
        ctrl.joinCurrentGame("niko");
        ctrl.joinCurrentGame("babe");

        lobby = ctrl.startCurrentGame().getBody();
    }

    @Test
    public void lobbyIsEmpty() {
        var actual = ctrl.getCurrentGame().getBody();
        assertEquals(0, actual.getPlayers().size());
    }

    @Test
    public void addNullNickName() {
        var actual = ctrl.joinCurrentGame(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addEmptyNickName() {
        var actual = ctrl.joinCurrentGame("    ");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addValidNickName() {
        final String nick = "johny";
        var actual = ctrl.joinCurrentGame(nick);
        assertEquals(OK, actual.getStatusCode());
        assertEquals(nick, actual.getBody().getNick());
    }

    @Test
    public void addValidNickNameTwice() {
        final String nick = "johny";
        var actual = ctrl.joinCurrentGame(nick);
        actual = ctrl.joinCurrentGame(nick);
        assertEquals(403, actual.getStatusCode().value());
    }

    @Test
    public void startTheEmptyLobby() {
        var actual = ctrl.startCurrentGame();
        assertEquals(405, actual.getStatusCode().value());
    }

    @Test
    public void startTheLobby() {
        ctrl.joinCurrentGame("johny");
        ctrl.joinCurrentGame("niko");
        ctrl.joinCurrentGame("babe");

        var newLobby = ctrl.startCurrentGame();

        assertEquals(ctrl.getAll().size(), 2);
        assertEquals(lobby.getPlayers().size(), 3);
        assertNotEquals(lobby, newLobby.getBody());
    }

    @Test
    public void leaderboardSorted() {
        List<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(0).setScore(4 * i + 2);
        }

        List<Player> expected = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed()).toList();

        assertEquals(ctrl.getLeaderboard(game.getId()).getBody().getRanking(), expected);
    }

    @Test
    public void getQuestion() {
        GameRepository repository = new GameRepository();
        List<Question> questions = Arrays.asList(
                new Question("this is q1", Paths.get("INVALID"),
                        new String[] {"answer 1", "answer 2", "answer 2" }, 0),
                new Question("this is q2", Paths.get("INVALID"),
                        new String[] {"answer 1", "answer 2", "answer 2" }, 0),
                new Question("this is q3", Paths.get("INVALID"),
                        new String[] {"answer 1", "answer 2", "answer 2" }, 0));
        Collections.shuffle(questions,
                new Random(repository.generateSeed(game.getId())));
        List<Question> repoQuestions = ctrl.getQuestions(game.getId()).getBody();
        assertEquals(repoQuestions.get(0), questions.get(0));
        assertEquals(repoQuestions.get(1), questions.get(1));
        assertEquals(repoQuestions.get(2), questions.get(2));
    }
}
