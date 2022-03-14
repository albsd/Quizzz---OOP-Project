package client.scenes;

import client.utils.ServerUtils;
import commons.Emote;
import commons.EmoteMessage;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import client.Main;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;

public class GameMultiplayerController implements Initializable {

    @Autowired
    private final ServerUtils server;

    private Player me;

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
    }

    public void setMe(final Player me) {
        this.me = me;
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
    public void returnMenu(final ActionEvent e) {
        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
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
}
