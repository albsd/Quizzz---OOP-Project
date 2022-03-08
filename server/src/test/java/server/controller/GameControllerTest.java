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
import static org.springframework.http.HttpStatus.NOT_FOUND;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.Random;


public class GameControllerTest {

    private GameService service;

    private GameController ctrl;

    private UUID uuid;

    @BeforeEach
    public void setup() {
        service = new GameService(new GameRepository());
        ctrl = new GameController(service);
        uuid = ctrl.create();
    }

    @Test
    public void addNullGame() {
        var actual = ctrl.join(null, "johny");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void addNullNickName() {
        var actual = ctrl.join(uuid, null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void joinUninitializedGameWithValidNickname() {
        var actual = ctrl.join(UUID.randomUUID(), "nick");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void joinInitializedGameWithValidNickname() {
        var actual = ctrl.join(uuid, "nick");
        assertEquals(OK, actual.getStatusCode());
    }
    @Test
    public void leaderboardEmpty() {
        assertEquals(service.
                getLeaderboard(uuid).getRanking(),
                new ArrayList<Player>());
    }
    @Test
    public void leaderboardSorted() {
        var player1 = ctrl.join(uuid, "player1");
        var player2 = ctrl.join(uuid, "player2");
        Game game = service.findById(uuid);
        List<Player> players = game.getPlayers();
        final int four = 4;
        final int two  = 2;
        players.get(0).setScore(four);
        players.get(1).setScore(two);
        List<Player> expected = Arrays.asList(
                player1.getBody(), player2.getBody());
        assertEquals(ctrl.getLeaderboard(uuid).
                getBody().getRanking(), expected);
    }
    @Test
    public void getQuestion() {
        GameRepository repository = new GameRepository();
        List<Question> questions = Arrays.asList(
                new Question("this is q1", Paths.get("INVALID"),
                new String[]{"answer 1", "answer 2", "answer 2"}, 0),
                new Question("this is q2", Paths.get("INVALID"),
                new String[]{"answer 1", "answer 2", "answer 2"}, 0),
                new Question("this is q3", Paths.get("INVALID"),
                new String[]{"answer 1", "answer 2", "answer 2"}, 0));
        Collections.shuffle(questions,
                new Random(repository.generateSeed(uuid)));
        List<Question> repoQuestions = ctrl.getQuestions(uuid).getBody();
        assertEquals(repoQuestions.get(0), questions.get(0));
        assertEquals(repoQuestions.get(1), questions.get(1));
        assertEquals(repoQuestions.get(2), questions.get(2));
    }
}
