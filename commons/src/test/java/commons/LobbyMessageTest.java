package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LobbyMessageTest {

    private String nick1;
    private String nick2;
    private final int time1 = 12;
    private final int time2 = 24;
    private String content1;
    private String content2;
    private LobbyMessage message1;
    private LobbyMessage message2;


    @BeforeEach
    void setup() {
        nick1 = "Adam";
        nick2 = "Kevin";
        content1 = "Lets start the game!";
        content2 = "Waiting for a friend";
        message1 = new LobbyMessage(nick1, time1, content1);
        message2 = new LobbyMessage(nick2, time2, content1);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, message1.getNick());
        assertEquals(time1, message1.getTimestamp());
        assertEquals(content1, message1.getContent());

        assertEquals(nick2, message2.getNick());
        assertEquals(time2, message2.getTimestamp());
        assertEquals(content2, message2.getContent());
    }
}
