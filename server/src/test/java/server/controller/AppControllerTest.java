package server.controller;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.repository.ActivityRepository;
import server.repository.GameRepository;
import server.repository.LeaderboardRepository;
import server.service.ActivityService;
import server.service.GameService;
import server.service.LeaderboardService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AppControllerTest {

    private AppController ctrl;

    private final List<Question> questions = List.of(
            new MultipleChoiceQuestion("test_prompt", new byte[23],
                    new String[]{"Option1", "Option2", "Option3"}, 1));

    private Game game;

    private Player p1;

    private Player p2;

    private Player p3;

    @BeforeEach
    public void setup() {
        p1 = new Player("Charlie");
        p2 = new Player("Speedy");
        p3 = new Player("Kelly");
        GameService gameService = new GameService(new GameRepository());
        gameService.initializeLobby(questions);
        ctrl = new AppController(gameService, null);
        game = gameService.getCurrentGame();
        game.addPlayer(p1);
        game.addPlayer(p2);
        gameService.newGame(questions);
        Game lobby = gameService.getCurrentGame();
        lobby.addPlayer(p3);
    }

    @Test
    void updateLobbyPlayerTime() {
        assertEquals(p3, ctrl.updateLobbyPlayerTime("Kelly"));
        long millisecondDif = new Date().getTime() - p3.getTimestamp().getTime();
        assertTrue(5000L > millisecondDif);
    }

    @Test
    void updateGamePlayerTime() {
        assertEquals(p1, ctrl.updateGamePlayerTime(game.getId(), "Charlie"));
        long millisecondDif = new Date().getTime() - p3.getTimestamp().getTime();
        assertTrue(5000L > millisecondDif);
    }
}
