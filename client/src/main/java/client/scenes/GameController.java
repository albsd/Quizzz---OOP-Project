package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;
import commons.Player;
import commons.Game;
import commons.EmoteMessage;
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
import javafx.scene.layout.AnchorPane;
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
    private Label question, questionNumber, points, timer1, timer2;

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
    private Label warning;

    private ProgressBarController progressBar;
    
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
    
    private final Font font;

    private Player me;

    private Game game;

    private String chatPath;

    private boolean isOpenQuestion = false;
    
    private String leavePath;

    private boolean doubleScore = false;

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

        question.setFont(font);
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
        
        // update scrollpane's layout before scrolling to the bottom
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

        displayCurrentQuestion();
        game.start(this::setNextQuestion);
    }

    public void setSinglePlayer(final Game game) {
        this.me = game.getPlayers().get(0); // only 1 player
        this.game = game;
        this.game.initialiseTimer();
        
        displayCurrentQuestion();
        game.start(this::setNextQuestion);

        leftBox.getChildren().remove(1);
        mainHorizontalBox.getChildren().remove(3, 5);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPrefWidth(600);
        optionBox.setPadding(Insets.EMPTY);
        optionBox.setSpacing(55);
    }


    /**
     * Validates the answer for the multiple choice question and open question
     * Updates the user's score given in what time frame he/she has answered.
     *
     * @param event triggered by a button click
     */
    @FXML
    public void checkAnswer(final ActionEvent event) {
        Question currentQuestion = game.getCurrentQuestion();
        long correctAnswer = currentQuestion.getAnswer();
        Button[] options = {option1, option2, option3};
        int option = ArrayUtils.indexOf(options, event.getSource());
        int score = 0;
        if (currentQuestion instanceof MultipleChoiceQuestion) {
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

        // Displays leaderboard at end of game
        if (game.isOver()) {
            if (!game.isMultiplayer()) {
                server.sendGameResult(this.me.getNick(), this.me.getScore());
                displayLeaderboardMomentarily(server.getSinglePlayerLeaderboard());
            } else {
                displayLeaderboardMomentarily(server.getLeaderboard(game.getId()));
            }
        }

        // Displays leaderboard every 10 questions in multiplayer
        if (game.isMultiplayer() && game.shouldShowMultiplayerLeaderboard()) {
            displayLeaderboardMomentarily(server.getLeaderboard(game.getId()));
        } else if (!game.isOver()) {
            Platform.runLater(this::displayCurrentQuestion);
            game.start(this::setNextQuestion);
        }
    }

    private void displayLeaderboardMomentarily(final Leaderboard leaderboard) {
        Platform.runLater(() -> {
            leaderboardController.displayLeaderboard(leaderboard, me);
        });
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
            Platform.runLater(this::displayCurrentQuestion);
            game.start(this::setNextQuestion);
        }
    }

    private void displayCurrentQuestion() {
        Question currentQuestion = game.getCurrentQuestion();
        if (isOpenQuestion && currentQuestion instanceof MultipleChoiceQuestion) {
            changeToMultiMode();
            isOpenQuestion = false;
        } else if (!isOpenQuestion && currentQuestion instanceof FreeResponseQuestion) {
            changeToFreeMode();
            isOpenQuestion = true;
        }

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

    private void changeToMultiMode() {
        openAnswer.toFront();
        openAnswer.setVisible(false);
        option1.setVisible(true);
        option2.setVisible(true);
        option3.setVisible(true);
    }

    private void changeToFreeMode() {
        openAnswer.toBack();
        openAnswer.setVisible(true);
        option1.setVisible(false);
        option2.setVisible(false);
        option3.setVisible(false);
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
}
