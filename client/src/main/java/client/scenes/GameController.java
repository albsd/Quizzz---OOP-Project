package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;

import commons.EmoteMessage;
import commons.Game;
import commons.MultipleChoiceQuestion;
import commons.Question;
import commons.FreeResponseQuestion;
import commons.Emote;
import commons.Player;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

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

    @FXML
    private TextField openAnswer;

    @FXML
    private Label warning;

    private final ServerUtils server;

    private final FXMLController fxml;

    private final ProgressBarController progressBar;

    private final Font font;

    private Player me;

    private Game game;

    private String chatPath;

    private Question currentQuestion;

    private boolean isOpenQuestion;
    
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
        //by default game.fxml set to multiple question mode
        currentQuestion = game.getCurrentQuestion();
        isOpenQuestion = !(currentQuestion instanceof MultipleChoiceQuestion);
        if (isOpenQuestion) {
            fxml.changeToFreeMode(openAnswer, option1, option2, option3);
        }
        displayQuestion(currentQuestion);
        // start client timer
        // progressBar.start();
        game.start(this::setNextQuestion);
    }

    public void setSinglePlayer(final Game game) {
        this.me = game.getPlayers().get(0); // only 1 player
        this.game = game;
        this.game.initialiseTimer();
        
        questionNumber.setText("#" + game.getCurrentQuestionNumber());
        game.start(this::setNextQuestion);

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
    @FXML
    public void checkAnswer(final ActionEvent event) {
        long correctAnswer = currentQuestion.getAnswer();
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
        server.addScore(game.getId(), me.getNick(), score);
        //TODO: Display correct answer for free response
    }

    /**
     * Set the next question, in case we are passed the 10th question
     * sends the player score and displays the leaderboard above the current screen.
     */
    @FXML
    public void setNextQuestion() {
        game.nextQuestion();
        
        if (game.shouldShowLeaderboard()) {
            Platform.runLater(() -> {
                var root = fxml.displayLeaderboardMomentarily();
                var ctrl = root.getKey();
                ctrl.displayLeaderboard(game.getId());
            });
        }

        Platform.runLater(() -> {
            currentQuestion = game.getCurrentQuestion();
//            question.setText(currentQuestion.getPrompt());
            if (isOpenQuestion && currentQuestion instanceof MultipleChoiceQuestion) {
                fxml.changeToMultiMode(openAnswer, option1, option2, option3);
                isOpenQuestion = false;
            } else if (!isOpenQuestion && currentQuestion instanceof FreeResponseQuestion) {
                fxml.changeToFreeMode(openAnswer, option1, option2, option3);
                isOpenQuestion = true;
            }
            displayQuestion(currentQuestion);
        });
        game.start(this::setNextQuestion);
    }

    private void displayQuestion(final Question currentQuestion) {
        questionNumber.setText("#" + (game.getCurrentQuestionIndex() + 1));
        question.setText(currentQuestion.getPrompt());
        if (currentQuestion instanceof MultipleChoiceQuestion) {
            String[] options = ((MultipleChoiceQuestion) currentQuestion).getOptions();
            option1.setText(options[0]);
            option2.setText(options[1]);
            option3.setText(options[2]);
        }
    }

    @FXML
    public void openPopup(final ActionEvent e) {
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
