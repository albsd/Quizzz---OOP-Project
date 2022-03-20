package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;

import commons.GameUpdate;
import commons.QuestionTimer;
import commons.Player;
import commons.Game;
import commons.Emote;
import commons.EmoteMessage;
import commons.Question;
import commons.MultipleChoiceQuestion;
import commons.FreeResponseQuestion;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable, WebSocketSubscription {

    @FXML
    private Button option1, option2, option3,
            timeButton, scoreButton, removeButton,
            cancelButton, confirmButton;
    
    @FXML
    private Label questionPrompt, questionNumber, points, popupText, timer1, timer2;

    @FXML
    private ProgressBar timer;

    @FXML
    private Pane popupMenu;

    @FXML
    private ScrollPane emoteScroll;

    @FXML
    private VBox emoteChat, leftBox, optionBox;

    @FXML
    private HBox mainHorizontalBox;

    private final ServerUtils server;

    private final FXMLController fxml;

    private final QuestionTimer gameTimer;
    private final QuestionTimer clientTimer;

    private final Font font;

    private Player me;

    private Game game;

    private String chatPath;

    private boolean doubleScore = false;

    @Inject
public GameController(final ServerUtils server, final FXMLController fxml) {
        this.gameTimer = new QuestionTimer(time -> { }, this::setNextQuestion);
        this.clientTimer = new QuestionTimer(
                time -> timer.setProgress((double) time / QuestionTimer.MAX_TIME), () -> { });
        this.server = server;
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        option1.setFont(font);
        option2.setFont(font);
        option3.setFont(font);

        cancelButton.setFont(font);
        confirmButton.setFont(font);
        popupText.setFont(font);

        questionPrompt.setFont(font);
        questionNumber.setFont(font);
        points.setFont(font);
        timer1.setFont(font);
        timer2.setFont(font);
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[2];
        subscriptions[0] = server.registerForMessages("/topic" + chatPath, EmoteMessage.class, message -> {
            Platform.runLater(() -> {
                Label nickname = new Label(message.getNick());
                nickname.setFont(font);
                
                String emotePath = switch (message.getContent()) {
                    case cry -> "/images/face-sad.png";
                    case frown -> "/images/face-frown.png";
                    case smile -> "/images/face-smile.png";
                    case surprised -> "/images/face-surprise.png";
                };
                ImageView emoteImage = new ImageView();
                emoteImage.setImage(new Image(emotePath));
                
                HBox emoteBox = new HBox(20);
                emoteBox.setAlignment(Pos.CENTER_RIGHT);
                emoteBox.getChildren().addAll(nickname, emoteImage);
                emoteChat.getChildren().add(emoteBox);
                
                // update scrollpane's layout before scrolling to the bottom
                emoteScroll.layout();
                emoteScroll.setVvalue(1);


            });
        });

        subscriptions[1] = server.registerForMessages("/topic/game/" + this.game.getId()
                + "/update", GameUpdate.class, update -> {
            System.out.println("Halve message received!");
            Platform.runLater(() -> {
                switch (update) {
                    case halveTimer -> clientTimer.halve();
                    case startTimer -> clientTimer.start(0);
                    default -> { }
                }
            });
        });
        return subscriptions;
    }

    /**
     * Assign the currentGame and myself as a Player.
     * Initialize the game's timer and start the game loop.
     * 
     * @param me    Player of myself
     * @param game  Current game that I'm a part of
     */
    public void setGame(final Player me, final Game game) {
        this.me = me;
        this.game = game;
        this.chatPath = "/game/" + game.getId() + "/chat";

        Platform.runLater(this::displayQuestion);

        clientTimer.start(0);
        gameTimer.start(0);
    }

    public void setSinglePlayer(final Player me) {
        this.me = me;
        leftBox.getChildren().remove(1);
        mainHorizontalBox.getChildren().remove(3, 5);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPrefWidth(600);
        optionBox.setPadding(Insets.EMPTY);
        optionBox.setSpacing(55);
    }

    @FXML
    public void returnToMenu(final ActionEvent e) {
        // TODO: confirmation dialog
        fxml.showSplash();
    }

    /**
     * Validates the answer for the multiple choice question and open question
     * Updates the user's score given in what time frame he/she has answered.
     *
     * @param event triggered by a button click
     */
    public void checkAnswer(final ActionEvent event) {
        int time = clientTimer.getCurrentTime();
        Question question = game.getCurrentQuestion();
        String option = ((Button) event.getSource()).getText();
        int score = 0;
        if (question instanceof MultipleChoiceQuestion mcQuestion) {
            if (option.equals(mcQuestion.getAnswer())) {
                score = me.calculateMulChoicePoints(time);
            }
        } else if (question instanceof FreeResponseQuestion frQuestion) {
            try {
                score = me.calculateOpenPoints(frQuestion.getAnswer(), Integer.parseInt(option), time);
            } catch (NumberFormatException e) {
                // TODO: Add label in FXML to inform user of incorrect input
                System.out.println("Invalid input, should be a number");
                return;
            }
        }
        // check for double score
        if (doubleScore) {
            score *= 2;
            doubleScore = false;
        }
        me.addScore(score);
    }

    /**
     * Set the next question, in case we are passed the 10th question
     * sends the player score and displays the leaderboard above the current screen.
     */
    @FXML
    public void setNextQuestion() {
        game.nextQuestion();
        if ((game.getCurrentQuestionIndex()) % 10 == 0) {
            server.updateScore(game.getId(), me.getNick(), Integer.toString(me.getScore()));
            Platform.runLater(() -> {
                var root = fxml.displayLeaderboardMomentarily();
                LeaderboardController leaderboardController = root.getKey();
                leaderboardController.displayLeaderboard(this.game.getId());
            });
        }

        // TODO: Show answer

        Platform.runLater(() -> timer.setProgress(0.0));
        clientTimer.start(5000);
        gameTimer.start(5000);

        // TODO: Add five second delay before showing next question

        try {
            Thread.sleep(5000);
            Platform.runLater(this::displayQuestion);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void displayQuestion() {
        questionNumber.setText("#" + (game.getCurrentQuestionIndex() + 1));
        questionPrompt.setText(game.getCurrentQuestion().getPrompt());
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion instanceof MultipleChoiceQuestion mcQuestion) {
            String[] options = mcQuestion.getOptions();
            option1.setText(options[0]);
            option2.setText(options[1]);
            option3.setText(options[2]);
        }
    }

    public void openPopup(final ActionEvent e) throws IOException {
        popupMenu.setVisible(true);
    }

    @FXML
    public void closePopup(final ActionEvent e) {
        popupMenu.setVisible(false);
    }

    @FXML
    public void timePowerup(final ActionEvent e) {
        server.send("/app/game/" + this.game.getId() + "/halve", GameUpdate.halveTimer);

        // Solution to ensure that the initiator's timer is not halved
        clientTimer.setCurrentTime(clientTimer.getCurrentTime() * 2);
        timeButton.setDisable(true);
    }

    @FXML
    public void scorePowerup(final ActionEvent e) {
        doubleScore = true;
        ((Button) e.getSource()).setDisable(true);
    }

    @FXML
    public void removePowerup(final ActionEvent e) {

    }

    @FXML
    public void frown(final ActionEvent e) {
        sendEmote(Emote.frown);
    }

    @FXML
    public void cry(final ActionEvent e) {
        sendEmote(Emote.cry);
    }

    @FXML
    public void smile(final ActionEvent e) {
        sendEmote(Emote.smile);
    }

    @FXML
    public void surprised(final ActionEvent e) {
        sendEmote(Emote.surprised);
    }

    private void sendEmote(final Emote emote) {
        server.send("/app" + chatPath, new EmoteMessage(me.getNick(), emote));
    }
}
