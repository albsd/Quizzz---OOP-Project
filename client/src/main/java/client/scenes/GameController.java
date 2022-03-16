package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;
import commons.Player;
import commons.ScoreMessage;
import commons.Emote;
import commons.EmoteMessage;
import commons.Game;
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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public class GameController implements Initializable, WebSocketSubscription {

    @FXML
    private Button option1, option2, option3,
            timeButton, scoreButton, removeButton,
            cancelButton, confirmButton;
    
    @FXML
    private Label question, questionNumber, points, popupText, timer1, timer2;

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

    private final ProgressBarController progressBar;

    private final Font font;

    private Player me;

    private Game game;

    private String chatPath;

    private boolean doubleScore = false;

    @Inject
    public GameController(final ServerUtils server, final FXMLController fxml,
            final ProgressBarController progressBar) {
        this.server = server;
        this.fxml = fxml;
        this.progressBar = progressBar;
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

        question.setFont(font);
        questionNumber.setFont(font);
        points.setFont(font);
        timer1.setFont(font);
        timer2.setFont(font);
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[1];
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
        this.game.initialiseTimer();
        this.chatPath = "/game/" + game.getId() + "/chat";
       
        questionNumber.setText("#" + (game.getCurrentQuestionIndex() + 1));
        // question.setText(game.getCurrentQuestion().getPrompt());
        // start client timer
        // progressBar.start();
        game.start(this::setNextQuestion);
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
        Question currentQuestion = game.getCurrentQuestion();
        long correctAnswer = game.getCurrentQuestion().getAnswer();
        String optionStr = ((Button) event.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        int score = 0;
        if (currentQuestion instanceof MultipleChoiceQuestion) {
            if (option == correctAnswer) {
                score = me.calculateMulChoicePoints(progressBar.getClientTime());
            }
        } else if (currentQuestion instanceof FreeResponseQuestion) {
            //assume the player always inputs a number
            score = me.calculateOpenPoints(correctAnswer, option, progressBar.getClientTime());
        }
        //check for doublescore
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
            server.updateScore(game.getId(), new ScoreMessage(me.getNick(), me.getScore()));
            Platform.runLater(() -> {
                var root = fxml.displayLeaderboardMomentarily();
                LeaderboardController leaderboardController = root.getKey();
                leaderboardController.displayLeaderboard(this.game.getId());
            });
        }

        Platform.runLater(() -> {
            questionNumber.setText("#" + (game.getCurrentQuestionIndex() + 1));
//            question.setText(game.getCurrentQuestion().getPrompt());
        });
        
        game.start(this::setNextQuestion);
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
