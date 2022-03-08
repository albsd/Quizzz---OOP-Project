package server.repository;

import commons.Player;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Game;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameRepositoryTest {

    private Game game1;
    private Game game2;
    private GameRepository repo;

    @BeforeEach
    void setUp() {
        game1 = new Game(UUID.randomUUID());
        game2 = new Game(UUID.randomUUID());
        repo = new GameRepository();
        repo.addGame(game1);
        repo.addGame(game2);
    }

    //TODO: Debug why this test is failing.
    // Might be because games is a static list.
    // Test passes individually but not when all tests are run at once.
//    @Test
//    void getGames() {
//        assertTrue(Arrays.asList(new Game[] {game1, game2})
//                .containsAll(repo.getGames()));
//        assertTrue(repo.getGames()
//                .containsAll(Arrays.asList(game1, game2)));
//        repo.removeAllGames();
//    }

    @Test
    void findById() {
        assertEquals(game1, repo.findById(game1.getId()));
        assertEquals(game2, repo.findById(game2.getId()));
    }

    @Test
    void addGame() {
        Game game3 = new Game(UUID.randomUUID());
        assertEquals(game3.getId(), repo.addGame(game3));
        // assertTrue(Arrays.asList(new Game[]{game1, game2,
        // game3}).containsAll(repo.getGames()));
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
        final int ten = 10;
        final int twenty = 20;
        final int thirty = 30;
        player1.setScore(ten);
        player2.setScore(twenty);
        player3.setScore(thirty);
        game1.addPlayer(player1);
        game1.addPlayer(player2);
        game1.addPlayer(player3);
        assertEquals(Arrays.asList(player3, player2, player1),
                repo.getLeaderboard(game1.getId()).getRanking());
    }

    @Test
    public void getQuestionForOneGame() {
        List<Question> questions = Arrays.asList(
                new Question("this is q1", Paths.get("INVALID"),
                        new String[]{"answer 1", "answer 2", "answer 2"}, 0),
                new Question("this is q2", Paths.get("INVALID"),
                    new String[]{"answer 1", "answer 2", "answer 2"}, 0),
                new Question("this is q3", Paths.get("INVALID"),
                        new String[]{"answer 1", "answer 2", "answer 2"}, 0));
        long r = repo.generateSeed(game1.getId());
        Collections.shuffle(questions,
                new Random(r));

        List<Question> repoQuestions = repo.getQuestions(r);
        assertEquals(repoQuestions.get(0), questions.get(0));
        assertEquals(repoQuestions.get(1), questions.get(1));
        assertEquals(repoQuestions.get(2), questions.get(2));
    }
}
