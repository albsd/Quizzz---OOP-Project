package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    private UUID id1;
    private UUID id2;
    private List<Player> p1;
    private List<Player> p2;
    private List<Question> q1;
    private List<Question> q2;
    private Game g1;
    private Game g2;
    private String nick1;
    private Player player;
    private int questionIndex;

    @BeforeEach
    void setup() {
        nick1 = "Peter";
        player = new Player(nick1);
        p1 = new ArrayList<>();
        p1.add(new Player(nick1));
        p1.add(new Player("Harry"));
        p2 = List.of(new Player[]{
                new Player("test4")});
        q1 = List.of(new Question[]{
                new MultipleChoiceQuestion("title1", new byte[]{}, new String[]{}, 2),
                new MultipleChoiceQuestion("title2", new byte[]{}, new String[]{}, 1),
                new FreeResponseQuestion("title3", new byte[]{}, 25553)});
        q2 = List.of(new Question[]{
                new FreeResponseQuestion("title1", new byte[]{}, 132),
                new MultipleChoiceQuestion("title2", new byte[]{}, new String[]{}, 0),
                new FreeResponseQuestion("title3", new byte[]{}, 233434)});
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        questionIndex = 0;
        g1 = new Game(id1, p1, q1, questionIndex, true);
        g2 = new Game(id2, p2, q2, questionIndex, false);
    }

    @Test
    void testGetters() {
        assertEquals(p1, g1.getPlayers());
        assertEquals(q1.get(0), g1.getCurrentQuestion());
        assertEquals(questionIndex + 1, g1.getCurrentQuestionNumber());
        assertEquals(q1, g1.getQuestions());
        assertEquals(player, g1.getPlayerByNick(nick1));
        assertEquals(id1, g1.getId());
        assertEquals(p1, g1.getPlayers());
    }

    @Test
    void testNextQuestion() {
        g1.nextQuestion();
        assertEquals(2, g1.getCurrentQuestionNumber());
    }

    @Test
    void isMultiplayer() {
        assertTrue(g1.isMultiplayer());
    }

    @Test
    void shouldShowMultiplayerLeaderboard() {
        assertFalse(g1.shouldShowMultiplayerLeaderboard());
    }

    @Test
    void addPlayer() {
        assertTrue(p1.add(new Player("Sam")));
    }

    @Test
    void removePlayer() {
        assertTrue(g1.removePlayer(player));
    }

    @Test
    void isOver() {
        assertFalse(g1.isOver());
    }

    @Test
    void setCurrentQuestionIndex() {
        g1.setCurrentQuestionIndex(12);
        assertEquals(13, g1.getCurrentQuestionNumber());
    }

    @Test
    void testSameGame() {
        assertEquals(g1, g1);
    }

    @Test
    void testDifferentGameEquals() {
        assertEquals(g1, new Game(id1, p1, q1, questionIndex, true));
    }

    @Test
    void testDifferentGameNotEquals() {
        assertNotEquals(g1, g2);
    }
}
