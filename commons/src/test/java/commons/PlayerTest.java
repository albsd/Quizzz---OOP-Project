package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    private Player p;

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
        p.setScore(80);
        assertEquals(80, p.getScore());
        p.setScore(120);
        assertEquals(120, p.getScore());
        p.setScore(0);
        assertEquals(0, p.getScore());
    }
}
