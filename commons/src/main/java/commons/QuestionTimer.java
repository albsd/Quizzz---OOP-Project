package commons;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class QuestionTimer {
    public static final int MAX_TIME = 20000;
    private final int decrement = 7;    // 7ms
    private int currentTime = MAX_TIME;
    private boolean started = false;
    private boolean over = false;
    private final Timer timer;
    private TimerTask currentTask;
    private final Consumer<Integer> timeDisplayer;
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
        System.out.println("Timer started.");
    }

    public void stop() {
        over = true;
        if (currentTask != null) {
            currentTask.cancel();
        }
    }

    public void halve() {
        if (started) {
            if (over) {
                System.out.println("Timer already finished!");
            } else {
                System.out.println("Time halved.");
                currentTime /= 2;
            }
        } else {
            System.out.println("Timer not started yet!");
        }
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
}
