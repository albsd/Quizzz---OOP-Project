package commons;

import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTimerTest {
    @Test
    void getCurrentTime() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);

        assertEquals(20000, questionTimer.getCurrentTime());
    }

    @Test
    void getMaxTime() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);

        assertEquals(20000, questionTimer.getMaxTime());
    }

    @Test
    void getId() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);

        assertEquals(id, questionTimer.getId());
    }

    @Test
    void getTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        Timer timer = new Timer();
        questionTimer.setTimer(timer);
        assertEquals(timer, questionTimer.getTimer());
    }

    @Test
    void getTask() {
        UUID id = new UUID(0, 1);

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hey!");
            }
        };

        QuestionTimer questionTimer = new QuestionTimer(id, task1);

        TimerTask task2 = questionTimer.getTask();

        assertEquals(task1, task2);
    }

    @Test
    void getDecrement() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);

        assertEquals(25, questionTimer.getDecrement());
    }

    @Test
    void getOneSecond() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);

        assertEquals(1000, questionTimer.getOneSecond());
    }

    @Test
    void setCurrentTime() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        questionTimer.setCurrentTime(20.5);
        assertEquals(20.5, questionTimer.getCurrentTime());
    }

    @Test
    void setTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
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

        QuestionTimer questionTimer = new QuestionTimer(id);

        questionTimer.setCurrentTask(task1);

        TimerTask task2 = questionTimer.getTask();

        assertEquals(task2, task1);
    }

    @Test
    void setStarted() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        questionTimer.setStarted(true);
        assertTrue(questionTimer.isStarted());
    }

    @Test
    void setOver() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        questionTimer.setOver(true);
        assertTrue(questionTimer.isOver());
    }

    @Test
    void isStarted() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        assertFalse(questionTimer.isStarted());
    }

    @Test
    void isOver() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        assertFalse(questionTimer.isOver());
    }

    @Test
    void startGameTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        questionTimer.startGameTimer();
    }

    @Test
    void stopGameTimer() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        questionTimer.startGameTimer();
        questionTimer.stopGameTimer();
    }

    @Test
    void halve() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
        questionTimer.setStarted(true);
        questionTimer.halve();
        assertEquals(10000, questionTimer.getCurrentTime());
    }

    @Test
    void reset() {
        UUID id = new UUID(0, 1);
        QuestionTimer questionTimer = new QuestionTimer(id);
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

    @Test
    void stop() {

    }
}