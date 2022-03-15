package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    private Player p;
    private final int testNumber1 = 80;
    private final int testNumber2 = 100;

    @BeforeEach
    void setup() {
        p = new Player("numpy as np");
    }

    @Test
    void testGetters() {
        assertEquals(0, p.getScore());
    }

    @Test
    void testScore() {
        p.addScore(testNumber1);
        assertEquals(testNumber1, p.getScore());
        p.addScore(testNumber2);
        assertEquals(testNumber1 + testNumber2, p.getScore());
        p.setScore(0);
        assertEquals(0, p.getScore());
    }
}
