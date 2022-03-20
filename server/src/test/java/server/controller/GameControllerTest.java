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
import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import server.service.ActivityService;
import server.repository.ActivityRepository;
import server.repository.GameRepository;
import server.service.GameService;


import java.util.List;
import java.util.Comparator;
@DataJpaTest
public class GameControllerTest {

    private Game lobby;

    private Game game;

    private GameController ctrl;

    @Mock
    private ActivityRepository activityRepository;

    private List<Activity> activities = List.of(new Activity());

    private String nick;

    private int score;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(activityRepository.count()).thenReturn(100L);
        when(activityRepository.findAll()).thenReturn(activities);

        GameService service =  new GameService(new GameRepository());
        ActivityService activityService = new ActivityService(activityRepository);
        service.initializeLobby(activityService.getQuestionList());

        ctrl = new GameController(service, activityService);
        // The current lobby is promoted to a game
        // a new lobby is returned after promotion
        game = service.getCurrentGame();
        ctrl.joinCurrentGame("johny");
        ctrl.joinCurrentGame("niko");
        ctrl.joinCurrentGame("babe");
        nick = "johny";
        score = 50;
        lobby = ctrl.startCurrentGame().getBody();
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
    public void updatePoints() {
        Game current = ctrl.updatePlayerPoints(game.getId(), nick, Integer.toString(score)).getBody();
        assertEquals(score, current.getPlayerByNick(nick).getScore());
    }

}
