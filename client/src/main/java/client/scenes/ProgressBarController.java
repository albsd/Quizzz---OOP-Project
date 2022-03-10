package client.scenes;

//import javafx.application.Platform;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//import java.util.Timer;
//import java.util.TimerTask;

public class ProgressBarController implements Initializable {
    @FXML
    private ProgressBar bar = new ProgressBar(1);

    @FXML
    private Label label;

    @FXML
    private Button option1;

    @FXML
    private Button option2;

    @FXML
    private Label verifLabel;

    private QuestionTimer questionTimer = new QuestionTimer();


    public void initialize(final URL location, final ResourceBundle resources) {
        start();
    }

    public void halve() {
        questionTimer.halve();
    }

    public void reset() {
        questionTimer.reset();
        Platform.runLater(() ->
                label.setText(String.valueOf(questionTimer.getCurrentTime()
                        / questionTimer.getOneSecond())));
        Platform.runLater(() ->
                bar.setProgress(questionTimer.getCurrentTime()
                        / questionTimer.getMaxTime()));
    }

    public void start() {
        List<Button> buttons = new ArrayList<>();
        buttons.add(option1);
        buttons.add(option2);
        questionTimer.startClientTimer(label, bar, buttons);
    }

    public void onOptionClick() throws InterruptedException {
        Instant time = Instant.now();
        verifLabel.setText("Option chosen at " + time);
    }

}
