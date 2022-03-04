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
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        public void run() {

            currentTime -= decrement;
            if (currentTime <= 0) {
                Platform.runLater(() -> label.setText("Time's over!"));
                currentTime = 0;
                timer.cancel();
                timer.purge();
            }
            Platform.runLater(() -> label.setText(
                    String.format("%.2f", currentTime / oneSecond)));
            Platform.runLater(() -> bar.setProgress(currentTime / maxTime));
            if (currentTime <= 0) {
                currentTime = 0;
                Platform.runLater(() -> label.setText("Time's over!"));
            }

        }
    };

    public double getCurrentTime() {
        return currentTime;
    }

    public void start() {
        final int delay = 25;
        final int period = 25;
        timer.scheduleAtFixedRate(task, delay, period);
    }

    public Timer getTimer() {
        return timer;
    }

    public TimerTask getTask() {
        return task;
    }

    public void halve() {
        currentTime /= 2;
    }

}
