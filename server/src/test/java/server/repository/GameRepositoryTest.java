package server.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.Game;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameRepositoryTest {

    Game game1;
    Game game2;
    GameRepository repo;

    @BeforeEach
    void setUp() {
        game1 = new Game(UUID.randomUUID());
        game2 = new Game(UUID.randomUUID());
        repo = new GameRepository();
        repo.addGame(game1);
        repo.addGame(game2);
    }


    @Test
    void getGames() {
        assertEquals(Arrays.asList(new Game[]{game1, game2}), repo.getGames());
        repo.removeAllGames();
    }

    @Test
    void findById() {
        assertEquals(game1, repo.findById(game1.getId()));
        assertEquals(game2, repo.findById(game2.getId()));
    }

    @Test
    void addGame() {
        Game game3 = new Game(UUID.randomUUID());
        assertEquals(game3.getId(), repo.addGame(game3));
        assertEquals(Arrays.asList(new Game[]{game2, game1, game3}), repo.getGames());
        repo.removeAllGames();
    }

    @Test
    void removeGame() {
        assertTrue(repo.removeGame(game2.getId()));
        assertNull(repo.findById(game2.getId()));
        repo.removeAllGames();
    }
}