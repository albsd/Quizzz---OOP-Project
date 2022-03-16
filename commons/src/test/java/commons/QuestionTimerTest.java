package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class QuestionTimerTest {

    private QuestionTimer questionTimer;

    @BeforeEach
    void setup() {
        questionTimer = new QuestionTimer();
        assertFalse(questionTimer.isStarted());
        assertFalse(questionTimer.isOver());
    }

    @Test
    void getMaxTime() {
        assertEquals(20000, questionTimer.getMaxTime());
    }

    @Test
    void getTimer() {
        Timer timer = new Timer();
        questionTimer.setTimer(timer);
        assertEquals(timer, questionTimer.getTimer());
    }

    @Test
    void setCurrentTime() {
        questionTimer.setCurrentTime(20);
        assertEquals(20, questionTimer.getCurrentTime());
    }

    @Test
    void setTimer() {
        Timer timer = new Timer();
        questionTimer.setTimer(timer);
        assertEquals(timer, questionTimer.getTimer());
    }

    @Test
    void setCurrentTask() {
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hey!");
            }
        };

        questionTimer.setCurrentTask(task1);

        TimerTask task2 = questionTimer.getTask();

        assertEquals(task2, task1);
    }

    @Test
    void setStarted() {
        questionTimer.setStarted(true);
        assertTrue(questionTimer.isStarted());
    }

    @Test
    void setOver() {
        questionTimer.setOver(true);
        assertTrue(questionTimer.isOver());
    }

    @Test
    void startGameTimer() {
        questionTimer.startGameTimer(() -> { });
        if (questionTimer.getTask() != null) {
            questionTimer.getTask().cancel();
        }
    }

    @Test
    void stopGameTimer() {
        questionTimer.startGameTimer(() -> { });
        questionTimer.stopGameTimer();
    }

    @Test
    void halve() {
        questionTimer.setStarted(true);
        questionTimer.halve();
        assertEquals(10000, questionTimer.getCurrentTime());
    }

    @Test
    void reset() {
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hey!");
            }
        };
        questionTimer.setCurrentTask(task1);
        questionTimer.setCurrentTime(0);
        questionTimer.reset();

        assertEquals(20000, questionTimer.getCurrentTime());
    }
}
