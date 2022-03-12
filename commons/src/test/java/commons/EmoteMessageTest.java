package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EmoteMessageTest {

    private String nick1;
    private String nick2;
    private Emote emote1;
    private Emote emote2;
    private EmoteMessage message1;
    private EmoteMessage message2;


    @BeforeEach
    void setup() {
        nick1 = "Adam";
        nick2 = "Kevin";
        emote1 = Emote.cry;
        emote2 = Emote.smile;
        message1 = new EmoteMessage(nick1, emote1);
        message2 = new EmoteMessage(nick2, emote2);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, message1.getNick());
        assertEquals(emote1, message1.getContent());

        assertEquals(nick2, message2.getNick());
        assertEquals(emote2, message2.getContent());
    }

    @Test
    void testEquals() {
        assertEquals(message1, new EmoteMessage("Adam", Emote.cry));
    }
}
