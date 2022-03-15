package client.scenes;

//import javafx.application.Platform;

import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;

import com.google.inject.Inject;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

import commons.GameUpdate;
import commons.QuestionTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.TimerTask;

public class ProgressBarController implements Initializable, WebSocketSubscription {

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

    private ServerUtils server;

    private String gameId;

    private QuestionTimer questionTimer;

    @Inject
    public ProgressBarController(final ServerUtils server) {
        this.questionTimer = new QuestionTimer();
        this.server = server;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        start();
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[1];
        subscriptions[0] = server.registerForMessages("/topic/game/" + gameId + "/update", GameUpdate.class, update -> {
            System.out.println("Halve message received!");
            Platform.runLater(() -> {
                switch (update) {
                    case halveTimer -> questionTimer.halve();
                    case stopTimer -> reset();
                    case startTimer -> start();
                    default -> { }
                }
            });
        });
        return subscriptions;
    }

    public void setGameId(final String id) {
        this.gameId = id;
    }

    private TimerTask clientTimerTask(final QuestionTimer questionTimer) {
        return new TimerTask() {
            @Override
            public void run() {
                questionTimer.setCurrentTime(
                        questionTimer.getCurrentTime() - questionTimer.getDecrement());
                if (questionTimer.getCurrentTime() <= 0) {
                    questionTimer.setOver(true);
                    System.out.println("Time's over!");
                    questionTimer.setCurrentTime(0);
                    cancel();
                }
            }
        };
    }

    public void startClientTimer(final QuestionTimer questionTimer) {
        if (questionTimer.isStarted()) {
            System.out.println("Timer already started! Reset first.");
        } else {
            System.out.println("Timer started.");
            questionTimer.setStarted(true);
            questionTimer.setOver(false);

            final int period = questionTimer.getDecrement();

            questionTimer.setCurrentTask(clientTimerTask(
                    questionTimer));
            questionTimer.getTimer().scheduleAtFixedRate(
                    questionTimer.getTask(), 0, period);
        }
    }

    @FXML
    public void reset() {
        questionTimer.reset();
        Platform.runLater(() -> label.setText(String.valueOf(questionTimer.getCurrentTime()
                / questionTimer.getOneSecond())));
        bar.setProgress((double) questionTimer.getCurrentTime()
                / questionTimer.getMaxTime());
    }

    @FXML
    public void start() {
        startClientTimer(questionTimer);
    }

    @FXML
    public void onOptionClick(final ActionEvent e) {
        Instant time = Instant.now();
        verifyLabel.setText("Option chosen at " + time);
    }

    @FXML
    public void onHalveButtonClick(final ActionEvent e) {
        server.send("/app/game/" + gameId + "/halve", GameUpdate.halveTimer);

        // Solution to ensure that the initiator's timer is not halved
        questionTimer.setCurrentTime(questionTimer.getCurrentTime() * 2);
        halveButton.setDisable(true);
    }

    public int getClientTime() {
        return questionTimer.getCurrentTime();
    }
}
