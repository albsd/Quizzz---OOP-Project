package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreMessageTest {

    private String nick1;
    private String nick2;
    private int score1;
    private int score2;
    private UUID id1;
    private UUID id2;
    private ScoreMessage message1;
    private ScoreMessage message2;

    @BeforeEach
    void setup() {
        nick1 = "Adam";
        nick2 = "Kevin";
        score1 = 300;
        score2 = 240;
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        message1 = new ScoreMessage(nick1, score1, id1);
        message2 = new ScoreMessage(nick2, score2, id2);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, message1.getNick());
        assertEquals(score1, message1.getContent());
        assertEquals(id1, message1.getId());

        assertEquals(nick2, message2.getNick());
        assertEquals(score2, message2.getContent());
        assertEquals(id2, message2.getId());
    }
}
