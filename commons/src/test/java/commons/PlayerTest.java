package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    Player p;

    @BeforeEach
    void setup() {
        p = new Player("numpy as np");
    }

    @Test
    void testGetters() {
        assertEquals("numpy as np", p.getName());
        assertEquals(0, p.getTime());
        assertEquals(0, p.getScore());
    }

    @Test
    void testTime() {
        p.setTime(20000);
        assertEquals(20000, p.getTime());
    }

    @Test
    void testScore() {
        p.addScore(80);
        assertEquals(80, p.getScore());
        p.addScore(100);
        assertEquals(180, p.getScore());
        p.setScore(0);
        assertEquals(0, p.getScore());
    }
}
