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
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import commons.Activity;
import commons.Game;
import commons.GameResult;
import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import server.repository.LeaderboardRepository;
import server.service.ActivityService;
import server.repository.ActivityRepository;
import server.repository.GameRepository;
import server.service.GameService;
import server.service.LeaderboardService;


import java.util.List;
import java.util.Comparator;
@DataJpaTest
public class GameControllerTest {

    private Game lobby;

    private Game game;

    private GameController ctrl;

    @Mock
    private ActivityRepository activityRepository;

    private List<Activity> activities = List.of(
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity(),
            new Activity());

    @Mock
    LeaderboardRepository leaderboardRepository;

    private List<GameResult> gameResults = List.of(new GameResult("nick", 0));

    private String nick;

    private int score;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(activityRepository.count()).thenReturn(20L);
        when(activityRepository.findAll()).thenReturn(activities);
        when(leaderboardRepository.findAll()).thenReturn(gameResults);

        GameService service =  new GameService(new GameRepository());
        ActivityService activityService = new ActivityService(activityRepository);
        LeaderboardService leaderboardService = new LeaderboardService(leaderboardRepository);
        service.initializeLobby(activityService.getQuestionList());

        ctrl = new GameController(service, activityService, leaderboardService);
        // The current lobby is promoted to a game
        // a new lobby is returned after promotion
        game = service.getCurrentGame();
        ctrl.joinCurrentGame("johny");
        ctrl.joinCurrentGame("niko");
        ctrl.joinCurrentGame("babe");
        nick = "johny";
        score = 50;
        ctrl.startLobby();
        lobby = ctrl.getCurrentGame();
    }

    @Test
    public void lobbyIsEmpty() {
        var actual = ctrl.getCurrentGame();
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
        var actual = ctrl.joinCurrentGame(nick);
        assertEquals(OK, actual.getStatusCode());
        assertEquals(nick, actual.getBody().getNick());
    }

    @Test
    public void addValidNickNameTwice() {
        var actual = ctrl.joinCurrentGame(nick);
        actual = ctrl.joinCurrentGame(nick);
        assertEquals(403, actual.getStatusCode().value());
    }

    @Test
    public void startTheLobby() {
        ctrl.joinCurrentGame("johny");
        ctrl.joinCurrentGame("niko");
        ctrl.joinCurrentGame("babe");

        ctrl.startLobby();

        assertEquals(ctrl.getAll().size(), 2);
        assertEquals(lobby.getPlayers().size(), 3);
        assertNotEquals(lobby, ctrl.getCurrentGame());
    }

    @Test
    public void leaderboardSorted() {
        List<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).addScore(4 * i + 2);
        }

        List<Player> expected = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed()).toList();

        assertEquals(ctrl.getLeaderboard(game.getId()).getBody().getRanking(), expected);
    }

    @Test
    public void updatePoints() {
        Game current = ctrl.addPlayerPoints(game.getId(), nick, Integer.toString(score)).getBody();
        assertEquals(score, current.getPlayerByNick(nick).getScore());
    }

//TODO:Make spring initialize activityService
//
//    @Test
//    public void getQuestionsNull() {
//
//        assertEquals(ctrl.getQuestions(game.getId()).getStatusCode(), 400);
//    }

//    @Test
//    public void getQuestionsNotNull() {
//        GameRepository gr = new GameRepository();
//        GameService gs = new GameService(gr, as);
//        List<Question> questions = ctrl.getQuestions(game.getId()).getBody();
//        assertEquals(questions, gs.getQuestions(game.getId()));
//    }
}
