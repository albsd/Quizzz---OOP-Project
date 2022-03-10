package client.scenes;

//import javafx.application.Platform;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.GameUpdate;

import commons.QuestionTimer;
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
    private Label verifyLabel;

    @FXML
    private Button halveButton;

    private QuestionTimer questionTimer = new QuestionTimer();

    @Inject
    public ProgressBarController(final ServerUtils server) {
        this.server = server;
        server.registerForMessages("/topic/game/update",
                GameUpdate.class, updateConsumer);
    }

    private Consumer<GameUpdate> updateConsumer = update -> {
        System.out.println("Halve message received!");
        Platform.runLater(() -> {
            if (update.getUpdate() == GameUpdate.Type.halveTimer) {
                questionTimer.halve();
            } else if (update.getUpdate() == GameUpdate.Type.stopTimer) {
                reset();
            } else if (update.getUpdate() == GameUpdate.Type.startTimer) {
                start();
            }
        });
    };

    public void initialize(final URL location, final ResourceBundle resources) {
        start();
    }

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
                    Platform.runLater(() -> {
                        label.setText(
                                String.format("%.2f",
                                        questionTimer.getCurrentTime()
                                                / questionTimer.getOneSecond())
                        );

                        bar.setProgress(questionTimer.getCurrentTime()
                                / questionTimer.getMaxTime()
                        );
                    });
                }
            }
        };
    }

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
                    questionTimer.getCurrentTask(), delay, period);
        }
    }

    public void reset() {
        questionTimer.reset();
        Platform.runLater(() ->
                label.setText(String.valueOf(questionTimer.getCurrentTime()
                        / questionTimer.getOneSecond())));
                bar.setProgress(questionTimer.getCurrentTime()
                        / questionTimer.getMaxTime());
    }

    public void start() {
        List<Button> buttons = new ArrayList<>();
        buttons.add(option1);
        buttons.add(option2);
        startClientTimer(questionTimer, label, bar, buttons);
    }

    public void onOptionClick() {
        Instant time = Instant.now();
        verifyLabel.setText("Option chosen at " + time);
    }

    public void onHalveButtonClick() {
        server.send("/app/halve",
                new GameUpdate(GameUpdate.Type.halveTimer));

        // Solution to ensure that the client's timer is not halved.
        // (if he was the one that clicked on the button)
        questionTimer.setCurrentTime(questionTimer.getCurrentTime() * 2);

        halveButton.setDisable(true);
    }

}
