package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.GameUpdate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ProgressBarController implements Initializable {

    private final ServerUtils server;

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

    private ClientQuestionTimer questionTimer = new ClientQuestionTimer();


    @Inject
    public ProgressBarController(final ServerUtils server) throws IOException {
        this.server = server;
        server.registerForMessages("/topic/game/update",
                GameUpdate.class, updateConsumer);
        halveButton.setOnAction(keyEvent -> {
            //double tempTime = ClientQuestionTimer.getCurrentTime();
            server.send("/app/halve",
                    new GameUpdate(GameUpdate.Update.halveTimer));
            halveButton.setDisable(true);
            // ClientQuestionTimer.
        });

    }

    private Consumer<GameUpdate> updateConsumer = update -> {
        System.out.println("Halve message received!");
        Platform.runLater(() -> {
            if (update.getUpdate() == GameUpdate.Update.halveTimer) {
                questionTimer.halve();
            } else if (update.getUpdate() == GameUpdate.Update.stopTimer) {
                reset();
            } else if (update.getUpdate() == GameUpdate.Update.startTimer) {
                start();
            }
        });
    };


    public void initialize(final URL location, final ResourceBundle resources) {
        start();
    }

    public void halve() {

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
