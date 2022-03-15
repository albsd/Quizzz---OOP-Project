package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreMessageTest {

    private String nick1;
    private String nick2;
    private int time1;
    private int time2;
    private UUID id1;
    private UUID id2;
    private String type1;
    private String type2;
    private int answer1;
    private int answer2;
    private int option1;
    private int option2;
    private ScoreMessage message1;
    private ScoreMessage message2;

    @BeforeEach
    void setup() {
        nick1 = "Adam";
        nick2 = "Kevin";
        time1 = 20000;
        time2 = 10000;
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        type1 = "multiple";
        type2 = "open";
        answer1 = 150;
        answer2 = 3000;
        option1 = 400;
        option2 = 2700;
        message1 = new ScoreMessage(nick1, time1, type1, answer1, option1, id1);
        message2 = new ScoreMessage(nick2, time2, type2, answer2, option2, id2);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, message1.getNick());
        assertEquals(time1, message1.getContent());
        assertEquals(id1, message1.getId());
        assertEquals(answer1, message1.getAnswer());
        assertEquals(option1, message1.getOption());
        assertEquals(id1, message1.getId());

        assertEquals(nick2, message2.getNick());
        assertEquals(time2, message2.getContent());
        assertEquals(id2, message2.getId());
        assertEquals(answer2, message2.getAnswer());
        assertEquals(option2, message2.getOption());
        assertEquals(id2, message2.getId());
    }
}