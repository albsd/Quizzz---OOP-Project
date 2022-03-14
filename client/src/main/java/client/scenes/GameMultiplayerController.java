package client.scenes;

import client.utils.ServerUtils;
import commons.ScoreMessage;
import commons.Emote;
import commons.EmoteMessage;
import commons.Game;
import commons.Player;
import javafx.application.Platform;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import client.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

public class GameMultiplayerController implements Initializable {

    // TODO: inject the ProgressBar.fxml into this scene

    @FXML
    private Button option1, option2, option3,
            timeButton, scoreButton, removeButton,
            cancelButton, confirmButton;
    @FXML
    private Label question, questionNumber, points, popupText, timer1, timer2;

    @FXML
    private Pane popupMenu;

    @FXML
    private ScrollPane emoteScroll;

    @FXML
    private VBox emoteChat;
    private Stage stage;

    private Scene scene;

    private final ServerUtils server;

    private Player me;
    private Game currentGame;

    @Inject
    public GameMultiplayerController(final ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Font font = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), 24);

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

        Consumer<EmoteMessage> emoteConsumer = msg -> Platform.runLater(() -> {
            Label nickname = new Label(msg.getNick());
            nickname.setFont(font);
            String emotePath = switch (msg.getContent()) {
                case cry -> "/images/face-sad.png";
                case frown -> "/images/face-frown.png";
                case smile -> "/images/face-smile.png";
                case surprised -> "/images/face-surprise.png";
            };
            ImageView emoteImage = new ImageView();
            emoteImage.setImage(new Image(emotePath));

            HBox message = new HBox(20);
            message.setAlignment(Pos.CENTER_RIGHT);
            message.getChildren().addAll(nickname, emoteImage);

            emoteChat.getChildren().add(message);

            // layout is needed to make sure the scroll pane is updated before scrolling to the bottom
            emoteScroll.layout();
            emoteScroll.setVvalue(1);
        });
        server.registerForMessages("/topic/game/chat", EmoteMessage.class, emoteConsumer);

        currentGame.start();
        questionNumber.setText("#1");
        question.setText(currentGame.getCurrentQuestion().getPrompt());
        currentGame.startTimer(()->{
            //calback
            setNextQuestion();
        });
    }

    @FXML
    public void returnMenu(final ActionEvent e) {
        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()) .getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    //this is for multiple choice. Also sets player's time
    public void checkMulChoiceAnswer(final ActionEvent e) {
        me.setTime(currentGame.getQuestionTime());
        int correctAnswer = currentGame.getCurrentQuestion().getAnswer();
        String optionStr = ((Button) e.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        if (option == correctAnswer) {
            calculateMulChoicePoints();
            System.out.println("Correct answer!");
        } else {
            System.out.println("Wrong answer. No points");
        }
        sendScores();
    }
    //this is for open questions
    public void checkOpenAnswer(final ActionEvent e) {
        me.setTime(currentGame.getQuestionTime());
        int correctAnswer = currentGame.getCurrentQuestion().getAnswer();
        String optionStr = ((Button) e.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        calculateOpenPoints(correctAnswer, option);
        sendScores();
    }

    private void calculateMulChoicePoints() {
        int base = 50;
        int bonusScore = (me.getTime() / 1000) * 2;
        me.setScore(base + bonusScore);
    }

    private void calculateOpenPoints(final int answer, final int option) {
        int bonusScore = (me.getTime() / 1000) * 2;
        int offPercentage = (int) Math.round(((double) Math.abs((option - answer)) / answer) * 100);
        int accuracyPercentage = 100 - offPercentage;
        int base = (accuracyPercentage % 10) * 10;
        me.setScore(base + bonusScore);
    }

    public void setMe(final Player me) {
        this.me = me;
    }

    public void setGame(final Game game) {
        this.currentGame = game;
    }

    @FXML
    public void setNextQuestion() {
        currentGame.nextQuestion(); //increments question by one
        question.setText(currentGame.getCurrentQuestion().getPrompt());
        questionNumber.setText( "#" +  1 + currentGame.getQuestionNumber());
        currentGame.startTimer(()->{
            setNextQuestion();
        });
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
        server.send("/app/game/chat", new EmoteMessage(me.getNick(), emote));
    }

    //Send it after every question
    private void sendScores() {
        server.send("/app/game/scores",  new ScoreMessage(me.getNick(), me.getScore(), currentGame.getId()));
    }
}
