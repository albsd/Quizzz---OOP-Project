package server.controller;

import commons.Game;
import commons.MultipleChoiceQuestion;
import commons.Player;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.repository.AppRepository;
import server.repository.GameRepository;
import server.service.GameService;
import server.service.AppService;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppControllerTest {

    private AppController ctrl;

    private final List<Question> questions = List.of(
            new MultipleChoiceQuestion("test_prompt", new byte[23], null,
                    new String[]{"Option1", "Option2", "Option3"}, 1));

    private Game game;

    private Player p1;

    private Player p2;

    private Player p3;

    private String mac1;

    private String mac2;

    private String nick1;

    @BeforeEach
    public void setup() {
        p1 = new Player("Charlie");
        p2 = new Player("Speedy");
        p3 = new Player("Kelly");
        nick1 = "deVito";
        mac1 = "E2_43_F2_J6_O9_3F";
        mac2 = "A8_43_G2_J6_O9_0F";
        GameService gameService = new GameService(new GameRepository());
        AppService appService = new AppService(new AppRepository());
        gameService.initializeLobby(questions);
        ctrl = new AppController(gameService, appService, null);
        game = gameService.getLobby();
        game.addPlayer(p1);
        game.addPlayer(p2);
        gameService.addLobby();
        gameService.newGame(questions);
        Game lobby = gameService.getLobby();
        lobby.addPlayer(p3);
    }

    @Test
    void updateLobbyPlayerTime() {
        assertEquals(p3, ctrl.updateLobbyPlayerTime("Kelly"));
        long millisecondDif = new Date().getTime() - p3.getTimestamp().getTime();
        assertTrue(10000 > (int) millisecondDif);
    }

    @Test
    void updateGamePlayerTime() {
        assertEquals(p1, ctrl.updateGamePlayerTime(game.getId(), "Charlie"));
        long millisecondDif = new Date().getTime() - p3.getTimestamp().getTime();
        assertTrue(10000 > (int) millisecondDif);
    }

    @Test
    void testSaveNickname() {
        assertEquals(new Player(nick1), ctrl.saveNickname(mac1, nick1));
    }

    @Test
    void testGetNickname() {
        ctrl.saveNickname(mac1, nick1);
        assertEquals(new Player(nick1), ctrl.getNickname(mac1).getBody());
    }
}
