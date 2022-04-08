package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class QuestionTimerTest {

    private QuestionTimer questionTimer;

    @BeforeEach
    void setup() {
        questionTimer = new QuestionTimer((time) -> { }, () -> { });
    }

    @Test
    void setCurrentTime() {
        questionTimer.setCurrentTime(15);
        assertEquals(15, questionTimer.getCurrentTime());
    }

    @Test
    void halve() {
        questionTimer.start(0);
        questionTimer.halve();
        assertTrue(questionTimer.getCurrentTime() <= 7500 && questionTimer.getCurrentTime() >= 7000);
    }

    @Test
    void getCurrentTime() {
        assertEquals(15000, questionTimer.getCurrentTime());
        questionTimer.setCurrentTime(10000);
        assertEquals(10000, questionTimer.getCurrentTime());
    }

    @Test
    void isOver() {
        questionTimer.start(0);
        questionTimer.setCurrentTime(4000);
        assertFalse(questionTimer.isOver());
        try {
            Thread.sleep(5000);
            assertTrue(questionTimer.isOver());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
