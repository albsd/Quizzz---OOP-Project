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
                    currentTime = 0;

                    // This is where the server should
                    // send a message to the client QuestionTimers
                    // that the timer ended



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

                // This is where the server sends the message
                // to all client QuestionTimers to halve

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

        // This is where the server sends the message
        // to all QuestionTimers to reset

        started = false;
        currentTime = maxTime;
    }

    public void stop() {
        currentTime = 0;
    }

}
