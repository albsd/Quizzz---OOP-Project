package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.function.Consumer;

import javax.inject.Inject;

public class GameController implements Initializable {

    private final ServerUtils server;

    private final FXMLController fxml;

    private Player me;

    private final ProgressBarController progressBar;

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
    private VBox emoteChat, leftBox, optionBox;

    @FXML
    private HBox mainHorizontalBox;

    private Game currentGame;

    @Inject
    public GameController(final ServerUtils server,
                          final FXMLController fxml,
                          final ProgressBarController progressBar) {
        this.server = server;
        this.fxml = fxml;
        this.progressBar = progressBar;
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

            // layout is needed to make sure the scroll pane is updated before scrolling to
            // the bottom
            emoteScroll.layout();
            emoteScroll.setVvalue(1);
        });
        server.registerForMessages("/topic/game/chat", EmoteMessage.class, emoteConsumer);

        questionNumber.setText("#1");
        question.setText(((Question) currentGame.getCurrentQuestion()).getPrompt());
        //start client timer
        progressBar.start();
        //start game timer and set gamestate to playing
        currentGame.start(this::setNextQuestion);
    }

    public void setSingle() {
        leftBox.getChildren().remove(1);
        mainHorizontalBox.getChildren().remove(3, 5);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPrefWidth(600);
        optionBox.setPadding(Insets.EMPTY);
        optionBox.setSpacing(55);
    }

    @FXML
    public void returnToMenu(final ActionEvent e) {
        fxml.showSplash();
    }

    //this is for multiple choice. Also sets player's time
    public void checkMulChoiceAnswer(final ActionEvent e) {
        int correctAnswer = ((MultipleChoiceQuestion) currentGame.getCurrentQuestion()).getAnswer();
        String optionStr = ((Button) e.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        if (option == correctAnswer) {
            System.out.println("Correct answer!");
            sendScores(me.getNick(), progressBar.getClientTime(), "multiple",
                    correctAnswer, option, currentGame.getId());
        } else {
            System.out.println("Wrong answer. No points");
        }
    }
    //this is for open questions
    public void checkOpenAnswer(final ActionEvent e) {
        long correctAnswer = ((FreeResponseQuestion) currentGame.getCurrentQuestion()).getAnswer();
        String optionStr = ((Button) e.getSource()).getText();
        long option;
        try {
            option = Integer.parseInt(optionStr);
        } catch (NumberFormatException exception) {
            System.out.println("invalid input");
            //set for 0 accuracy
            option = correctAnswer * -200;
        }
        sendScores(me.getNick(), progressBar.getClientTime(),
                "open", (int) correctAnswer, (int) option, currentGame.getId());
    }

    public void setMe(final Player me) {
        this.me = me;
    }

    public void setGame(final Game game) {
        this.currentGame = game;
    }

    @FXML
    public void setNextQuestion() {
        //increment and return next question
        questionNumber.setText("#" + (1 + currentGame.nextQuestion()));
        question.setText(((Question) currentGame.getCurrentQuestion()).getPrompt());
        //start client timer
        progressBar.start();
        //start game timer
        currentGame.start(this::setNextQuestion);
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

    private void sendScores(final String nick, final int time, final String type,
                            final int answer, final int option, final UUID id) {
        server.send("/app/game/scores",  new ScoreMessage(nick, time, type, answer, option, id));
    }
}
