package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {

    private String nick1;
    private String nick2;
    private final int time1 = 12;
    private final int time2 = 24;
    private String content1;
    private String content2;
    private Message message1;
    private Message message2;


    @BeforeEach
    void setup() {
        nick1 = "Adam";
        nick2 = "Kevin";
        content1 = "Lets start the game!";
        content2 = "Waiting for a friend";
        message1 = new Message(nick1, time1, content1);
        message2 = new Message(nick1, time1, content1);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, message1.getNick());
        assertEquals(time1, message1.getTime());
        assertEquals(content1, message1.getMessageContent());
    }
}
