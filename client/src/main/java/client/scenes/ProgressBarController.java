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

    /*public void setBar(ProgressBar bar) {
        this.bar = bar;
    }*/

    double currentTime = 20000;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            currentTime -= 25;
            if(currentTime <= 0){
                //System.out.println("Time's over!");
                Platform.runLater(() -> label.setText("Time's over!"));
                currentTime = 0;
                timer.cancel();
                timer.purge();
            }
            //System.out.println(currentTime / 1000);
            Platform.runLater(() -> label.setText(String.format("%.2f", currentTime / 1000)));
            Platform.runLater(() -> bar.setProgress(currentTime / 20000));
            if(currentTime <= 0){
                currentTime = 0;
                Platform.runLater(() -> label.setText("Time's over!"));
            }
            //bar.setProgress(currentTime / 2000);

        }
    };

    public void start(){
        timer.scheduleAtFixedRate(task, 25, 25);
    }

    public void halve(){
        currentTime /= 2;
    }

}