package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private String nick1;
    private String nick2;
    private int time1;
    private int time2;
    private String content1;
    private String content2;
    private Message message1;
    private Message message2;


    @BeforeEach
    void setup() {
        nick1 = "Adam";
        time1 = 12;
        content1 = "Lets start the game!";
    }

    @Test
    void testGetters() {

    }

    @Test
    void testSetters() {

    }
}