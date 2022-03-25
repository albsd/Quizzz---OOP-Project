package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;;

public class PlayerTest {

    private Player p1, p2;

    private String nick1, nick2;

    private Date time1;

    @BeforeEach
    void setup() {
        time1 = new Date();
        nick1 = "Morpheus";
        nick2 = "Trinity";
        p1 = new Player(nick1);
        p1.updateTimestamp(time1);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, p1.getNick());
        assertEquals(0, p1.getScore());
        assertEquals(time1, p1.getTimestamp());
    }

    @Test
    void testAlive() {
        p1.updateTimestamp(new Date());
        assertTrue(p1.isAlive());
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
    void testEqualsSameObject() {
        assertEquals(p1, p1);
    }

    @Test
    void testEqualsDifferentObject() {
        p2 = new Player(nick1);
        assertEquals(p1, p2);
    }

    @Test
    void testNotEqualsDifferentObject() {
        p2 = new Player(nick2);
        assertNotEquals(p1, p2);
    }
}
