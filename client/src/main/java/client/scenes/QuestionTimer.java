package commons;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.List;
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
                    // send a message to the client(s)
                    // that the timer ended
                    // (and call their own cancel functions)

                    cancel();
                }
            }
        };

    }

    private TimerTask clientTimerTask(
            final Label label, final ProgressBar bar,
            final List<Button> buttons) {
        return new TimerTask() {
            @Override
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    over = true;
                    System.out.println("Time's over!");
                    Platform.runLater(() -> label.setText("Time's over!"));
                    currentTime = 0;
                    for (Button b : buttons) {
                        b.setDisable(true);
                    }
                    cancel();
                } else {
                    Platform.runLater(() -> label.setText(
                            String.format("%.2f", currentTime / oneSecond)));
                    Platform.runLater(() ->
                            bar.setProgress(currentTime / maxTime));
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
        return over; }

    public void startClientTimer(final Label label, final ProgressBar bar,
                                 final List<Button> buttons) {
        if (started) {
            System.out.println("Timer already started! Reset first.");
        } else {
            System.out.println("Timer started.");
            started = true;
            over = false;
            final int delay = 0;
            final int period = 25;
            for (Button b : buttons) {
                b.setDisable(false);
            }
            currentTask = clientTimerTask(label, bar, buttons);
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
                System.out.println("Timer already finished!");
            } else {
                System.out.println("Time halved.");
                currentTime /= 2;
            }
        } else {
            System.out.println("Timer not started yet!");
        }
    }

    /*public void reset(final Label label, final ProgressBar bar) {
        System.out.println("Reset.");
        currentTask.cancel();
        started = false;
        currentTime = maxTime;
        Platform.runLater(() ->
                label.setText(String.valueOf(currentTime / oneSecond)));
        Platform.runLater(() ->
                bar.setProgress(currentTime / maxTime));
    }*/

    public void reset() {
        over = false;
        System.out.println("Reset timer.");
        currentTask.cancel();
        started = false;
        currentTime = maxTime;
    }

}
