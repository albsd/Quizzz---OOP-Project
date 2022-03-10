package commons;

import java.util.Timer;
import java.util.TimerTask;

public class QuestionTimer {
    private final double maxTime = 20000;
    private final double oneSecond = 1000;
    private final double decrement = 25;    // 25ms

    private double currentTime = maxTime;
    private boolean started = false;
    private boolean over = false;

    private Timer timer = new Timer();
    private TimerTask currentTask;

    private TimerTask serverTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    System.out.println("Time's over!");
                    stopServerTimer();
                    cancel();
                }
            }
        };

    }

    public double getCurrentTime() {
        return currentTime;
    }

    public double getOneSecond() {
        return oneSecond;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public boolean getOver() {
        return over;
    }

    public void startServerTimer() {
        if (started) {
            System.out.println("Server timer already started! Reset first.");
        } else {
            // This is where the server sends the message
            // to all client QuestionTimers to start

            System.out.println("Server timer started.");
            started = true;
            over = false;
            final int delay = 0;
            final int period = 25;
            currentTask = serverTimerTask();
            timer.scheduleAtFixedRate(currentTask, delay, period);

        }
    }

    public Timer getTimer() {
        return timer;
    }

    public TimerTask getTask() {
        return currentTask;
    }

    public void halve() {
        if (started) {
            if (over) {
                System.out.println("Server timer already finished!");
            } else {
                System.out.println("Time halved.");
                currentTime /= 2;
            }
        } else {
            System.out.println("Server timer not started yet!");
        }
    }

    public void reset() {
        over = false;
        System.out.println("Reset server timer.");
        currentTask.cancel();

        started = false;
        currentTime = maxTime;
    }

    public void stop() {
        currentTime = 0;
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

    public double getDecrement() {
        return decrement;
    }

    public TimerTask getCurrentTask() {
        return currentTask;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(final boolean started) {
        this.started = started;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(final boolean over) {
        this.over = over;
    }

    public void stopServerTimer() {
        this.currentTime = 0;
        this.over = true;
    }
}
