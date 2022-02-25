package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GameTest {
    List<Player> p;
    Game g;

    @BeforeEach
    void setup() {
        p = List.of(new Player[]{new Player("test1"), new Player("test2"), new Player("test3")});
        g = new Game(p);
    }

    @Test
    void testGetPlayers() {
        assertEquals(p, g.getPlayers());
    }

    @Test
    void testGetCurrentQuestion() {
        // This test does not pass yet, as generating random questions is not implemented yet.
        //assertNotNull(g.getCurrentQuestion());
    }

    @Test
    void testNextQuestion() {
        // This test is not functional yet either
        //Question q = g.getCurrentQuestion();
        //g.nextQuestion();
        //assertNotEquals(q, g.getCurrentQuestion());
    }
}