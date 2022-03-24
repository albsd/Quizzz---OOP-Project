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
import commons.FreeResponseQuestion;
import commons.MultipleChoiceQuestion;
import commons.Leaderboard;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;

public class GameController implements Initializable, WebSocketSubscription {

    @FXML
    private Button option1, option2, option3,
            timeButton;
    
    @FXML
    private Label questionPrompt, questionNumber, points, timer1, timer2, warning, answerBox;

    @FXML
    private ProgressBar timer;

    @FXML
    private ScrollPane emoteScroll;

    @FXML
    private VBox emoteChat, leftBox, optionBox;

    @FXML
    private AnchorPane menu;

    @FXML
    private HBox mainHorizontalBox;

    @FXML
    private ImageView questionImage;

    @FXML
    private TextField openAnswer;
    
    @FXML
    private Parent popup;

    @FXML
    private PopupController popupController;

    @FXML
    private Parent leaderboard;

    @FXML
    private LeaderboardController leaderboardController;

    private final ServerUtils server;
    
    private final FXMLController fxml;

    private final QuestionTimer gameTimer;

    private final QuestionTimer clientTimer;

    private final Font font;

    private Player me;

    private Game game;

    private String chatPath;

    private boolean isOpenQuestion = false;
    
    private String leavePath;

    private boolean doubleScore;

    private boolean submittedAnswer;

    private final String green = "#E0FCCF";

    private final String red = "#FE6F5B";

    private final String orange = "#FFD029";

    private final String darkGreen = "#009a19";

    private final String darkRed = "#A00000";

    @Inject
    public GameController(final ServerUtils server, final FXMLController fxml) {
        this.gameTimer = new QuestionTimer(time -> { }, this::setNextQuestion);
        this.clientTimer = new QuestionTimer(
                time -> Platform.runLater(() -> timer.setProgress((double) time / QuestionTimer.MAX_TIME)), () -> { });
        this.server = server;
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        option1.setFont(font);
        option2.setFont(font);
        option3.setFont(font);
        questionPrompt.setFont(font);
        questionNumber.setFont(font);
        points.setFont(font);
        timer1.setFont(font);
        timer2.setFont(font);
        answerBox.setFont(font);
        warning.setFont(font);
        submittedAnswer = false;
        doubleScore = false;
        openAnswer.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0,
                change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        }));
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[3];
        subscriptions[0] = server.registerForMessages("/topic" + chatPath, EmoteMessage.class, message -> {
            Platform.runLater(() -> {
                String emotePath = switch (message.getContent()) {
                    case cry -> "/images/face-sad.png";
                    case frown -> "/images/face-frown.png";
                    case smile -> "/images/face-smile.png";
                    case surprised -> "/images/face-surprise.png";
                    case reducedTime -> "/images/reduced-time.png";
                };
                
                updateEmoteBox(message.getNick(), emotePath);
            });
        });

        subscriptions[1] = server.registerForMessages("/topic" + leavePath, Player.class, player -> {
            Platform.runLater(() -> {
                updateEmoteBox(player.getNick(), "/images/disconnected.png");
            });
        });

        subscriptions[2] = server.registerForMessages("/topic/game/" + game.getId()
                + "/update", GameUpdate.class, update -> {
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
        this.chatPath = "/game/" + game.getId() + "/chat";
        this.leavePath = "/game/" + game.getId() + "/leave";

        displayCurrentQuestion();
        clientTimer.start(0);
        gameTimer.start(0);

        server.startTimerTask(new TimerTask() {
            @Override
            public void run() {
                server.updateGamePlayer(game.getId(), me.getNick());
            }
        });
    }

    public void setSinglePlayer(final Game game) {
        this.me = game.getPlayers().get(0); // only 1 player
        this.game = game;

        leftBox.getChildren().remove(1);
        mainHorizontalBox.getChildren().remove(3, 5);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPrefWidth(600);
        optionBox.setPadding(Insets.EMPTY);
        optionBox.setSpacing(55);

        displayCurrentQuestion();
        clientTimer.start(0);
        gameTimer.start(0);
    }

    @FXML
    public void checkMulChoiceOption(final ActionEvent e) {
        if (!submittedAnswer) {
            submittedAnswer = true;
            Button chosenOption = (Button) e.getSource();
            chosenOption.setStyle("-fx-background-color:" + green);
            Button[] options = {option1, option2, option3};
            long option = ArrayUtils.indexOf(options, chosenOption);
            checkAnswer(option, clientTimer.getCurrentTime());
        } else {
            warning.setVisible(true);
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
        if (!submittedAnswer) {
            submittedAnswer = true;
            long option = Long.parseLong(optionStr);
            checkAnswer(option, clientTimer.getCurrentTime());
        } else {
            warning.setVisible(true);
        }
    }

    /**
     * Validates and displays the answer for the multiple choice question.
     *
     * @param option the chosen answer to the question
     * @param time the time left in ms
     */
    @FXML
    public void checkAnswer(final long option, final int time) {
        int score = game.getCurrentQuestion().calculateScore(option, time);
        if (doubleScore) {
            score *= 2;
            doubleScore = false;
        }
        me.addScore(score);
        server.addScore(game.getId(), me.getNick(), score);
    }

    private void displayAnswerMomentarily() {
        Platform.runLater(() -> {
            timer.setProgress(0.0);
            points.setText("Total points: " + me.getScore());
        });
        warning.setVisible(false);
        long answer = game.getCurrentQuestion().getAnswer();
        if (!isOpenQuestion) {
            Button[] options = {option1, option2, option3};
            for (int i = 0; i < options.length; i++) {
                if (i == answer) {
                    options[i].setStyle("-fx-background-color:" + green + ";\n-fx-text-fill:" + darkGreen + ";");
                } else {
                    options[i].setStyle("-fx-background-color:" + red + ";\n-fx-text-fill:" + darkRed + ";");
                }
            }
        } else {
            Platform.runLater(() -> answerBox.setText("Answer is: " + answer));
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the next question, in case we are passed the 10th question
     * sends the player score and displays the leaderboard above the current screen.
     */
    @FXML
    public void setNextQuestion() {
        displayAnswerMomentarily();
        // Displays leaderboard at end of game
        if (game.isOver()) {
            if (!game.isMultiplayer()) {
                server.sendGameResult(this.me.getNick(), this.me.getScore());
                displayLeaderboardMomentarily(server.getSinglePlayerLeaderboard());
            } else {
                server.stopTimerTask();
                displayLeaderboardMomentarily(server.getLeaderboard(game.getId()));
                server.removeGame(game.getId());
            }
        }
        // Displays leaderboard every 10 questions in multiplayer
        if (game.isMultiplayer() && game.shouldShowMultiplayerLeaderboard()) {
            displayLeaderboardMomentarily(server.getLeaderboard(game.getId()));
        }
        if (!game.isOver()) {
            Platform.runLater(() -> {
                openAnswer.setText("");
                answerBox.setText("");
            });
            submittedAnswer = false;
            warning.setVisible(false);
            game.nextQuestion();
            displayCurrentQuestion();
            clientTimer.start(0);
            gameTimer.start(0);
        }
    }

    private void displayLeaderboardMomentarily(final Leaderboard leaderboard) {
        Platform.runLater(() -> leaderboardController.displayLeaderboard(leaderboard, me));
        menu.setVisible(false);
        leaderboardController.show();
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!game.isOver()) {
            leaderboardController.hide();
            menu.setVisible(true);
        }
    }

    private void displayCurrentQuestion() {
        Platform.runLater(() -> {
            Question currentQuestion = game.getCurrentQuestion();
            if (isOpenQuestion && currentQuestion instanceof MultipleChoiceQuestion) {
                changeToMultiMode();
                isOpenQuestion = false;
            } else if (!isOpenQuestion && currentQuestion instanceof FreeResponseQuestion) {
                changeToFreeMode();
                isOpenQuestion = true;
            }
            questionNumber.setText("#" + (game.getCurrentQuestionNumber()));
            questionPrompt.setText(currentQuestion.getPrompt());
            Image img = new Image(new ByteArrayInputStream(currentQuestion.getImage()));
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
        });
    }

    @FXML
    public void openPopup(final ActionEvent e) {
        popupController.open("game", () -> {
            server.stopTimerTask();
            server.leaveGame(me.getNick(), game.getId());
        });
    }

    @FXML
    public void timePowerup(final ActionEvent e) {
        server.send("/app/game/" + this.game.getId() + "/halve", GameUpdate.halveTimer);
        reducedTime();
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

    @FXML
    public void reducedTime() {
        sendEmote(Emote.reducedTime);
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
