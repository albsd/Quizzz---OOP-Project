package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LeaderboardTest {

    private Leaderboard leaderboard1;
    private Leaderboard leaderboard2;
    private List<Player> players1;
    private List<Player> players2;
    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;
    private Player p5;
    private Player p6;

    @BeforeEach
    void setup() {
        p1 = new Player("Garfield", 100);
        p2 = new Player("Kevin", 63);
        p3 = new Player("Sherly", 53);
        p4 = new Player("Keanu", 30);
        p5 = new Player("Batman", 20);
        p6 = new Player("Jason", 7);
        players1 = List.of(p6, p5, p4, p3, p2, p1);
        players2 = List.of(p1, p2, p3, p4, p5, p6);
        leaderboard1 = new Leaderboard(players1);
    }

    @Test
    void getRanking() {
        assertEquals(players2, leaderboard1.getRanking());
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(leaderboard1, leaderboard1);
    }
    @Test
    void testEqualsDifferentObject() {
        leaderboard2 = new Leaderboard(players1);
        assertEquals(leaderboard2, leaderboard1);
    }
    @Test
    void testNotEqualsDifferentObject() {
        List<Player> players3 = List.of(p2, p3, p4, p6);
        leaderboard2 = new Leaderboard(players3);
        assertNotEquals(leaderboard2, leaderboard1);
    }
}
