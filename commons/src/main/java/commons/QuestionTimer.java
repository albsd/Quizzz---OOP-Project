package commons;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class QuestionTimer {
    private final double maxTime = 20000;
    private final double oneSecond = 1000;
    private final double decrement = 25;    // 25ms
    private double currentTime = maxTime;
    private boolean started = false;
    private boolean over = false;
    private Timer timer;
    private TimerTask currentTask;
    private final UUID id;

    public QuestionTimer(final UUID id) {
        this.id = id;
        this.timer = new Timer();
    }

    public QuestionTimer(final UUID id, final TimerTask task) {
        this.id = id;
        this.timer = new Timer();
        this.currentTask = task;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public UUID getId() {
        return id;
    }

    public Timer getTimer() {
        return timer;
    }

    public TimerTask getTask() {
        return currentTask;
    }

    public double getDecrement() {
        return decrement;
    }

    public double getOneSecond() {
        return oneSecond;
    }

    public void setCurrentTime(final double currentTime) {
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

    public void startGameTimer() {
        reset();
        if (started) {
            System.out.println("Game timer already started! Reset first.");
        } else {
            System.out.println("Game timer started.");
            started = true;
            over = false;
            final int delay = 0;
            final int period = 25;
            currentTask = gameTimerTask();
            timer.scheduleAtFixedRate(currentTask, delay, period);
        }
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

    private TimerTask gameTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    System.out.println("Time's over!");
                    stopGameTimer();
                    cancel();
                }
            }
        };
    }
}
