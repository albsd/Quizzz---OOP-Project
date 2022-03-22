package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    private Player p1;

    @BeforeEach
    void setup() {
        p1 = new Player("numpy as np");
    }

    @Test
    void testGetters() {
        assertEquals("numpy as np", p1.getNick());
        assertEquals(0, p1.getScore());
    }

    @Test
    void testScores() {
        p1.addScore(80);
        assertEquals(80, p1.getScore());
        p1.addScore(120);
        assertEquals(200, p1.getScore());
        p1.addScore(0);
        assertEquals(200, p1.getScore());
    }

    @Test
    void testEquals() {
        assertEquals(p1, new Player("numpy as np"));
    }
}
