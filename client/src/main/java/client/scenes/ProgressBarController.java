package client.scenes;

<<<<<<< HEAD
//import javafx.application.Platform;

=======
import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.GameUpdate;

import commons.QuestionTimer;
>>>>>>> cf305ed77fcd8b371c3ba3bd426cd0befcf417b3
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
import java.util.TimerTask;
import java.util.function.Consumer;

public class ProgressBarController implements Initializable {

    private ServerUtils server;

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

    @FXML
    private Button halveButton;

    private QuestionTimer questionTimer = new QuestionTimer();

    @Inject
    public ProgressBarController(final ServerUtils server) {
        this.server = server;
        server.registerForMessages("/topic/game/update",
                GameUpdate.class, updateConsumer);
        halveButton.setOnAction(keyEvent -> {
            server.send("/app/halve",
                    new GameUpdate(GameUpdate.Update.halveTimer));
            halveButton.setDisable(true);
        });

    }

    private Consumer<GameUpdate> updateConsumer = update -> {
        System.out.println("Halve message received!");
        Platform.runLater(() -> {
            if (update.getUpdate() == GameUpdate.Update.halveTimer) {
                questionTimer.halve();
                // Solution for the half thing:
                // Save score or double on button click
                // (separate method linked to button)
            } else if (update.getUpdate() == GameUpdate.Update.stopTimer) {
                reset();
            } else if (update.getUpdate() == GameUpdate.Update.startTimer) {
                start();
            }
        });
    };

    private TimerTask clientTimerTask(final QuestionTimer questionTimer,
            final Label label, final ProgressBar bar,
            final List<Button> buttons) {
        return new TimerTask() {
            @Override
            public void run() {
                questionTimer.setCurrentTime(
                        questionTimer.getCurrentTime()
                                - questionTimer.getDecrement());
                if (questionTimer.getCurrentTime() <= 0) {
                    questionTimer.setOver(true);
                    System.out.println("Time's over!");
                    Platform.runLater(() -> label.setText("Time's over!"));
                    questionTimer.setCurrentTime(0);
                    for (Button b : buttons) {
                        b.setDisable(true);
                    }
                    cancel();
                } else {
                    Platform.runLater(() -> label.setText(
                            String.format("%.2f", questionTimer.getCurrentTime()
                                    / questionTimer.getOneSecond())));
                    Platform.runLater(() ->
                            bar.setProgress(questionTimer.getCurrentTime()
                                    / questionTimer.getMaxTime()));
                }
            }
        };
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void startClientTimer(final QuestionTimer questionTimer,
                                 final Label label,
                                 final ProgressBar bar,
                                 final List<Button> buttons) {
        if (questionTimer.isStarted()) {
            System.out.println("Timer already started! Reset first.");
        } else {
            System.out.println("Timer started.");
            questionTimer.setStarted(true);
            questionTimer.setOver(false);

            final int delay = 0;
            final int period = 25;
            for (Button b : buttons) {
                b.setDisable(false);
            }

            questionTimer.setCurrentTask(clientTimerTask(
                    questionTimer, label, bar, buttons));
            questionTimer.getTimer().scheduleAtFixedRate(
                    questionTimer.getCurrentTask(), 0, 25);
        }
    }

    public void initialize(final URL location, final ResourceBundle resources) {
        start();
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
        startClientTimer(questionTimer, label, bar, buttons);
    }

    public void onOptionClick() {
        Instant time = Instant.now();
        verifLabel.setText("Option chosen at " + time);
    }

}
