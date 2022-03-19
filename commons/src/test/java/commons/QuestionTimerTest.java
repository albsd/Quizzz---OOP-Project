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
}
