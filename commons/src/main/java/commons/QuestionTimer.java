package commons;

import java.util.Timer;
import java.util.TimerTask;

public class QuestionTimer {
    private final int maxTime = 1000;
    private final int oneSecond = 1000;
    private final int decrement = 25;    // 25ms
    private int currentTime = maxTime;
    private boolean started = false;
    private boolean over = false;
    private Timer timer;
    private TimerTask currentTask;

    public QuestionTimer() {
        this.timer = new Timer();
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public Timer getTimer() {
        return timer;
    }

    public TimerTask getTask() {
        return currentTask;
    }

    public int getDecrement() {
        return decrement;
    }

    public double getOneSecond() {
        return oneSecond;
    }

    public void setCurrentTime(final int currentTime) {
        this.currentTime = currentTime;
    }

    public void setTimer(final Timer timer) {
        this.timer = timer;
    }

    public void setCurrentTask(final TimerTask currentTask) {
        this.currentTask = currentTask;
    }

    public void setStarted(final boolean started) {
        this.started = started;
    }


    public void setOver(final boolean over) {
        this.over = over;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isOver() {
        return over;
    }

    public void startGameTimer(final Runnable callback) {
        reset();
        System.out.println("Game timer started.");
        started = true;
        over = false;
        final int delay = 0;
        currentTask = gameTimerTask(callback);
        timer.scheduleAtFixedRate(currentTask, delay, decrement);
    }

    public void stopGameTimer() {
        this.currentTime = 0;
        this.over = true;
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

    public void reset() {
        over = false;
        System.out.println("Reset timer.");
        if (currentTask != null) {
            currentTask.cancel();
        }
        started = false;
        currentTime = maxTime;
    }

    private TimerTask gameTimerTask(final Runnable callback) {
        return new TimerTask() {
            @Override
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    System.out.println("Time's over!");
                    try {
                        stopGameTimer();
                        callback.run();
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                    cancel();
                }
            }
        };
    }
}
