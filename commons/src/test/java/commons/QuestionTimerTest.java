package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTimerTest {

    private QuestionTimer questionTimer;

    @BeforeEach
    void setup() {
        questionTimer = new QuestionTimer(time -> { }, () -> { });
    }

    @Test
    void setCurrentTime() {
        questionTimer.setCurrentTime(20);
        assertEquals(20, questionTimer.getCurrentTime());
    }

    @Test
    void halve() {
        questionTimer.start(0);
        questionTimer.halve();
        assertTrue(questionTimer.getCurrentTime() <= 10000 && questionTimer.getCurrentTime() >= 9900);
    }

    @Test
    void isOver() {
        questionTimer.start(0);
        questionTimer.setCurrentTime(500);
        try {
            Thread.sleep(550);
            System.out.println(questionTimer.getCurrentTime());
            assertTrue(questionTimer.isOver());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
