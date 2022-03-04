package client.scenes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressBarController {
    @FXML
    private ProgressBar bar = new ProgressBar(1);

    @FXML
    private Button halveButton;

    @FXML
    private Button startButton;

    @FXML
    private Label label;

    private final double maxTime = 20000;
    private final double oneSecond = 1000;
    private final double decrement = 25;    // 25ms

    private double currentTime = maxTime;
    private boolean started = false;

    private Timer timer = new Timer();
    private TimerTask currentTask;

    private TimerTask newTimerTask() {
        return new TimerTask() {
            public void run() {
                currentTime -= decrement;
                if (currentTime <= 0) {
                    Platform.runLater(() -> label.setText("Time's over!"));
                    currentTime = 0;
                    cancel();
                }
                else {
                    Platform.runLater(() -> label.setText(
                            String.format("%.2f", currentTime / oneSecond)));
                    Platform.runLater(() -> bar.setProgress(currentTime / maxTime));
                }
            }
        };
    }

    /*private TimerTask task = new TimerTask() {
        public void run() {
            currentTime -= decrement;
            if (currentTime <= 0) {
                Platform.runLater(() -> label.setText("Time's over!"));
                currentTime = 0;
                task.cancel();
            }
            else{
                Platform.runLater(() -> label.setText(
                        String.format("%.2f", currentTime / oneSecond)));
                Platform.runLater(() -> bar.setProgress(currentTime / maxTime));
            }
        }
    };*/

    public double getCurrentTime() {
        return currentTime;
    }

    public void start() {
        if (started) {
            System.out.println("Already started! Reset first.");
        }
        else {
            started = true;
            final int delay = 25;
            final int period = 25;
            currentTask = newTimerTask();
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
        if (started){
            if (currentTime <= 0) {
                System.out.println("Already finished!");
            }
            else {
                currentTime /= 2;
            }
        }
        else {
            System.out.println("Not started yet!");
        }
    }

    public void reset() {
        currentTask.cancel();
        started = false;
        currentTime = maxTime;
        Platform.runLater(() ->
                label.setText(String.valueOf(currentTime / oneSecond)));
        Platform.runLater(() ->
                bar.setProgress(currentTime / maxTime));
    }

}
