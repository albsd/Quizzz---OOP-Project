package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;
import commons.EmoteMessage;
import commons.Game;
import commons.Player;
import commons.Question;
import commons.MultipleChoiceQuestion;
import commons.FreeResponseQuestion;
import commons.Leaderboard;
import commons.Emote;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable, WebSocketSubscription {

    @FXML
    private Button option1, option2, option3,
            timeButton, scoreButton, removeButton;
    
    @FXML
    private Label question, questionNumber, points, timer1, timer2, warning, answerBox;

    @FXML
    private ProgressBar timer;

    @FXML
    private ScrollPane emoteScroll;

    @FXML
    private VBox emoteChat, leftBox, optionBox;

    @FXML
    private HBox mainHorizontalBox;

    @FXML
    private ImageView questionImage;

    @FXML
    private TextField openAnswer;

    private ProgressBarController progressBar;
    
    @FXML
    private Parent popup;

    @FXML
    private PopupController popupController; 
    
    private final ServerUtils server;
    
    private final FXMLController fxml;
    
    private final Font font;

    private Player me;

    private Game game;

    private String chatPath;

    private Question currentQuestion;

    private boolean isOpenQuestion;
    
    private String leavePath;

    private boolean doubleScore = false;

    private boolean submittedAnswer = false;

    private final String green = "#E0FCCF";

    private final String red = "#FE6F5B";

    private final String orange = "#FFD029";

    private final String darkGreen = "#009a19";

    private final String darkRed = "#A00000";

    @Inject
    public GameController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        option1.setFont(font);
        option2.setFont(font);
        option3.setFont(font);
        answerBox.setFont(font);
        question.setFont(font);
        questionNumber.setFont(font);
        points.setFont(font);
        timer1.setFont(font);
        timer2.setFont(font);
        warning.setFont(font);
        warning.setStyle("-fx-text-fill:" + red + ";");
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[2];
        subscriptions[0] = server.registerForMessages("/topic" + chatPath, EmoteMessage.class, message -> {
            Platform.runLater(() -> {
                String emotePath = switch (message.getContent()) {
                    case cry -> "/images/face-sad.png";
                    case frown -> "/images/face-frown.png";
                    case smile -> "/images/face-smile.png";
                    case surprised -> "/images/face-surprise.png";
                };
                
                updateEmoteBox(message.getNick(), emotePath);
            });
        });

        subscriptions[1] = server.registerForMessages("/topic" + leavePath, Player.class, player -> {
            Platform.runLater(() -> {
                updateEmoteBox(player.getNick(), "/images/disconnected.png");
            });
        });

        return subscriptions;
    }

    /**
     * Updates the emotebox with the given event.
     * 
     * @param nick Player who sends an event
     * @param imagePath Image to be displayed as result of the event
     */
    private void updateEmoteBox(final String nick, final String imagePath) {
        Label nickname = new Label(nick);
        nickname.setFont(font);
    
        ImageView emoteImage = new ImageView();
        emoteImage.setImage(new Image(imagePath));

        HBox emoteBox = new HBox(20);
        emoteBox.setAlignment(Pos.CENTER_RIGHT);
        emoteBox.getChildren().addAll(nickname, emoteImage);
        emoteChat.getChildren().add(emoteBox);
        emoteScroll.layout();
        emoteScroll.setVvalue(1);
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
        this.leavePath = "/game/" + game.getId() + "/leave";

        //by default game.fxml set to multiple question mode
        currentQuestion = game.getCurrentQuestion();
        isOpenQuestion = !(currentQuestion instanceof MultipleChoiceQuestion);
        if (isOpenQuestion) {
            changeToFreeMode();
        }
        displayQuestion();
        
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

    /**
     * Validates and displays the answer for the multiple choice question.
     *
     * @param event triggered by a button click
     */
    @FXML
    public void checkMultipleQuestion(final ActionEvent event) {
        if (!submittedAnswer) {
            submittedAnswer = true;
            long correctAnswer = currentQuestion.getAnswer();
            Button[] options = {option1, option2, option3};
            int option = ArrayUtils.indexOf(options, ((Button) event.getSource()));
            int score = 0;
            Button chosenOption = (Button) event.getSource();
            chosenOption.setStyle("-fx-border-color: black;");
            for (int i = 0; i < options.length; i++) {
                if (i == correctAnswer) {
                    options[i].setStyle("-fx-background-color:" + green + ";\n-fx-text-fill:" + darkGreen + ";");
                } else {
                    options[i].setStyle("-fx-background-color:" + red + ";\n-fx-text-fill:" + darkRed + ";");
                }
            }
            if (option == correctAnswer) {
                score = me.calculateMulChoicePoints(progressBar.getClientTime());
            }
            checkForDoubleAndSend(score);
        } else {
            warning.setText("Already submitted answer");
        }
    }

    /**
     * Displays and validates the answer for open question.
     *
     * @param event triggered by a button click
     */
    @FXML
    public void onEnter(final ActionEvent event) {
        String optionStr = openAnswer.getText();
        if (!validateAnswer(optionStr)) {
            warning.setText("Type in only numbers");
            return;
        }
        warning.setText("");
        if (!submittedAnswer) {
            submittedAnswer = true;
            long correctAnswer = currentQuestion.getAnswer();
            answerBox.setText("Correct answer: " + correctAnswer);
            long option = Long.parseLong(optionStr);
            int score = me.calculateOpenPoints(correctAnswer, option, progressBar.getClientTime());
            checkForDoubleAndSend(score);
        } else {
            warning.setText("Already submitted answer");
        }
    }

    private boolean validateAnswer(final String answer) {
        return answer.matches("[0-9]*");
    }

    /**
     * Checks for double power up and send score.
     * @param score score of player for this question
     */
    private void checkForDoubleAndSend(final int score) {
        if (!doubleScore) {
            server.addScore(game.getId(), me.getNick(), score);
        } else {
            doubleScore = false;
            server.addScore(game.getId(), me.getNick(), score * 2);
        }
    }

    /**
     * Set the next question, in case we are passed the 10th question
     * sends the player score and displays the leaderboard above the current screen.
     */
    @FXML
    public void setNextQuestion() {
        game.nextQuestion();
        if (game.shouldShowSingleplayerLeaderboard()) {
            server.addScore(game.getId(), me.getNick(), me.getScore());
            Platform.runLater(() -> {
                Leaderboard singlePlayerLeaderboard = server.sendSinglePlayerLeaderboardInfo(this.me.getNick(),
                        this.me.getScore());
                var root = fxml.displayLeaderboardMomentarily();
                var ctrl = root.getKey();
                ctrl.displayLeaderboard(singlePlayerLeaderboard);
            });
        }
        if (game.getCurrentQuestionNumber() != 21) {
            Platform.runLater(() -> {
                submittedAnswer = false;
                warning.setText("");
                answerBox.setText("");
                openAnswer.setText("");
                currentQuestion = game.getCurrentQuestion();
                if (isOpenQuestion && currentQuestion instanceof MultipleChoiceQuestion) {
                    changeToMultiMode();
                    isOpenQuestion = false;
                } else if (!isOpenQuestion && currentQuestion instanceof FreeResponseQuestion) {
                    changeToFreeMode();
                    isOpenQuestion = true;
                }
                displayQuestion();
            });
            game.start(this::setNextQuestion);
        }
    }

    @FXML
    private void displayQuestion() {
        questionNumber.setText("#" + (game.getCurrentQuestionNumber()));
        question.setText(currentQuestion.getPrompt());
        InputStream is = new ByteArrayInputStream(currentQuestion.getImage());
        Image img = new Image(is);
        questionImage.setImage(img);
        if (currentQuestion instanceof MultipleChoiceQuestion) {
            String[] options = ((MultipleChoiceQuestion) currentQuestion).getOptions();
            option1.setStyle("-fx-background-color:" + orange + ";");
            option2.setStyle("-fx-background-color:" + orange + ";");
            option3.setStyle("-fx-background-color:" + orange + ";");
            option1.setText(options[0]);
            option2.setText(options[1]);
            option3.setText(options[2]);
        }
    }
    
    @FXML
    public void openPopup(final ActionEvent e) {
        popupController.open("game", () -> {
            server.leaveGame(me.getNick(), game.getId());
        });
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

    @FXML
    private void changeToMultiMode() {
        answerBox.toFront();
        openAnswer.toFront();
        openAnswer.setVisible(false);
        option1.setVisible(true);
        option2.setVisible(true);
        option3.setVisible(true);
    }

    @FXML
    private void changeToFreeMode() {
        answerBox.toBack();
        openAnswer.toBack();
        openAnswer.setVisible(true);
        option1.setVisible(false);
        option2.setVisible(false);
        option3.setVisible(false);
    }
}
