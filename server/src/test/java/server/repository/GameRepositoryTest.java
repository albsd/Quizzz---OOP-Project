package server.repository;

import commons.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Game;


import java.util.Arrays;

import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameRepositoryTest {

    private Game game1;
    private Game game2;
    private GameRepository repo;

    @BeforeEach
    void setUp() {
        game1 = new Game(UUID.randomUUID(), null);
        game2 = new Game(UUID.randomUUID(), null);
        repo = new GameRepository();
        repo.addGame(game1);
        repo.addGame(game2);
    }

    @Test
    void getGames() {
        assertTrue(Arrays.asList(new Game[] {game1, game2})
                .containsAll(repo.getGames()));
        assertTrue(repo.getGames()
                .containsAll(Arrays.asList(game1, game2)));
        repo.removeAllGames();
    }

    @Test
    void findById() {
        assertEquals(game1, repo.findById(game1.getId()));
        assertEquals(game2, repo.findById(game2.getId()));
    }

    @Test
    void addGame() {
        Game game3 = new Game(UUID.randomUUID(), null);
        assertEquals(game3.getId(), repo.addGame(game3));
        assertTrue(Arrays.asList(new Game[] {game1, game2,
                game3}).containsAll(repo.getGames()));
        repo.removeAllGames();
    }

    @Test
    void removeGame() {
        assertTrue(repo.removeGame(game2.getId()));
        assertNull(repo.findById(game2.getId()));
        repo.removeAllGames();
    }

    @Test
    void getLeaderboard() {
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Player player3 = new Player("player3");
        player1.addScore(10);
        player2.addScore(20);
        player3.addScore(30);
        game1.addPlayer(player1);
        game1.addPlayer(player2);
        game1.addPlayer(player3);
        assertEquals(Arrays.asList(player3, player2, player1),
                repo.getLeaderboard(game1.getId()).getRanking());
    }

}
