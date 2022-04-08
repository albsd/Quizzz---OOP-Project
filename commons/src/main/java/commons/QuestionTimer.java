package commons;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * This is the class used to time the questions during
 * both the single-player and multi-player modes.
 * By default, it's supposed to run for 15 seconds and
 * it "updates" once every 7ms and changing this
 * will reflect itself on not only the time the players have
 * but also on how its consumers behave
 * (such as the progress bar,
 * which may move by higher or lower increments).
 * The timer may also execute code when it's over
 * through the runnable in the constructor.
 */

public class QuestionTimer {
    public static final int MAX_TIME = 15000; // 15s
    private final int decrement = 7;    // 7ms
    private int currentTime = MAX_TIME;
    private boolean started = false;
    private boolean over = false;
    private final Timer timer;
    private TimerTask currentTask;
    private final Consumer<Integer> timeDisplayer; // object updated with current time value
    private final Runnable finishCallback;

    public QuestionTimer(final Consumer<Integer> timeDisplayer, final Runnable finishCallback) {
        this.timer = new Timer();
        this.timeDisplayer = timeDisplayer;
        this.finishCallback = finishCallback;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(final int currentTime) {
        this.currentTime = currentTime;
    }

    public void start(final int delay) {
        if (currentTask != null) {
            currentTask.cancel();
        }
        currentTime = MAX_TIME;
        started = true;
        over = false;
        currentTask = newTimerTask();
        timer.scheduleAtFixedRate(currentTask, delay, decrement);
    }

    public void halve() {
        if (started && !over) {
            currentTime /= 2;
        }
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.cancel();
        }
        timer.purge();
    }

    public void startDelay(final Runnable task) {
        currentTask = delayTask(task);
        timer.schedule(currentTask, 5000);
    }

    private TimerTask newTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    over = true;
                    currentTime = 0;
                    try {
                        finishCallback.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cancel();
                }
                try {
                    timeDisplayer.accept(currentTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private TimerTask delayTask(final Runnable task) {
        return new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
    }

    public boolean isOver() {
        return over;
    }
}
