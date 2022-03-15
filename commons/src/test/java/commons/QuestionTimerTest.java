package commons;

import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class QuestionTimerTest {
    @Test
    void getCurrentTime() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();

        assertEquals(20000, questionTimer.getCurrentTime());
    }

    @Test
    void getMaxTime() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();

        assertEquals(20000, questionTimer.getMaxTime());
    }

    @Test
    void getTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        Timer timer = new Timer();
        questionTimer.setTimer(timer);
        assertEquals(timer, questionTimer.getTimer());
    }

    @Test
    void getTask() {
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hey!");
            }
        };

        QuestionTimer questionTimer = new QuestionTimer(task1);

        TimerTask task2 = questionTimer.getTask();

        assertEquals(task1, task2);
    }

    @Test
    void getDecrement() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();

        assertEquals(25, questionTimer.getDecrement());
    }

    @Test
    void getOneSecond() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();

        assertEquals(1000, questionTimer.getOneSecond());
    }

    @Test
    void setCurrentTime() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        questionTimer.setCurrentTime(20);
        assertEquals(20, questionTimer.getCurrentTime());
    }

    @Test
    void setTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        Timer timer = new Timer();
        questionTimer.setTimer(timer);
        assertEquals(timer, questionTimer.getTimer());
    }

    @Test
    void setCurrentTask() {
        UUID id = new UUID(0, 1);

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hey!");
            }
        };

        QuestionTimer questionTimer = new QuestionTimer();

        questionTimer.setCurrentTask(task1);

        TimerTask task2 = questionTimer.getTask();

        assertEquals(task2, task1);
    }

    @Test
    void setStarted() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        questionTimer.setStarted(true);
        assertTrue(questionTimer.isStarted());
    }

    @Test
    void setOver() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        questionTimer.setOver(true);
        assertTrue(questionTimer.isOver());
    }

    @Test
    void isStarted() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        assertFalse(questionTimer.isStarted());
    }

    @Test
    void isOver() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        assertFalse(questionTimer.isOver());
    }

    @Test
    void startGameTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        questionTimer.startGameTimer(() -> { });
        if (questionTimer.getTask() != null) {
            questionTimer.getTask().cancel();
        }
    }

    @Test
    void stopGameTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        questionTimer.startGameTimer(() -> { });
        questionTimer.stopGameTimer();
    }

    @Test
    void halve() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
        questionTimer.setStarted(true);
        questionTimer.halve();
        assertEquals(10000, questionTimer.getCurrentTime());
    }

    @Test
    void reset() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer();
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
