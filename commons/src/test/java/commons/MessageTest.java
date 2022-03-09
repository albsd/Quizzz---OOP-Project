package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageTest {

    private Player p1;
    private Player p2;
    private final int time1 = 12;
    private final int time2 = 24;
    private String content1;
    private String content2;
    private Message message1;
    private Message message2;


    @BeforeEach
    void setup() {
        p1 = new Player("Adam", 0, 0);
        p2 = new Player("Kevin", 0, 0);
        content1 = "Lets start the game!";
        content2 = "Waiting for a friend";
        // TODO: Add LocalTime timestamps
        //message1 = new LobbyMessage(p1, time1, content1);
        //message2 = new LobbyMessage(p2, time2, content1);
    }

    @Test
    void testGetters() {
        //assertEquals(p1.getNick(), message1.getPlayer().getNick());
        //assertEquals(time1, message1.getTimestamp());
        //assertEquals(content1, message1.getContent());
    }
}
