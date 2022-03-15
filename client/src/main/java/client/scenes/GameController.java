package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.Player;
import commons.ScoreMessage;
import commons.Emote;
import commons.EmoteMessage;
import commons.Game;
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

import javax.inject.Inject;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public class GameController implements Initializable {

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

    public void setGame(final Player me, final Game game) {
        this.me = me;
        this.game = game;
        this.chatPath = "/game/" + game.getId() + "/chat";
       
        questionNumber.setText("#1");
        question.setText(game.getCurrentQuestion().getPrompt());
        // start client timer
        progressBar.start();
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

    // this is for multiple choice. Also sets player's time
    public void checkMulChoiceAnswer(final ActionEvent e) {
        int correctAnswer = game.getCurrentQuestion().getAnswer();
        String optionStr = ((Button) e.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        if (option == correctAnswer) {
            System.out.println("Correct answer!");
            sendScores(me.getNick(), progressBar.getClientTime(), "multiple", correctAnswer, option);
        } else {
            System.out.println("Wrong answer. No points");
        }
    }

    // this is for open questions
    public void checkOpenAnswer(final ActionEvent e) {
        int correctAnswer = game.getCurrentQuestion().getAnswer();
        String optionStr = ((Button) e.getSource()).getText();
        int option;
        try {
            option = Integer.parseInt(optionStr);
        } catch (NumberFormatException exception) {
            System.out.println("invalid input");
            // set for 0 accuracy
            option = correctAnswer * -200;
        }
        sendScores(me.getNick(), progressBar.getClientTime(), "open", correctAnswer, option);
    }

    public void setMe(final Player me) {
        this.me = me;
    }

    public void setGame(final Game game) {
        System.out.println("Game " + game);
        this.game = game;
        this.game.initialiseTimer();

        questionNumber.setText("#" + (game.getCurrentQuestionIndex() + 1));
        //question.setText(game.getCurrentQuestion().getPrompt());
        //start client timer
        //progressBar.start();
        game.start(this::setNextQuestion);
    }

    @FXML
    public void setNextQuestion() {
        game.nextQuestion();
        //logic to show leaderboard
        if ((game.getCurrentQuestionIndex()) % 10 == 0) {
            Platform.runLater(() -> {
                var root = fxml.displayLeaderboardMomentarily();
                LeaderboardController leaderboardController = root.getKey();
                leaderboardController.displayLeaderboard(this.game.getId());
            });
        }

        Platform.runLater(() -> {
            questionNumber.setText("#" + (game.getCurrentQuestionIndex() + 1));
            question.setText(game.getCurrentQuestion().getPrompt());
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

    private void sendScores(final String nick, final int time, final String type,
            final int answer, final int option) {
        server.updateScore(game.getId(), new ScoreMessage(nick, time, type, answer, option));
    }
}
