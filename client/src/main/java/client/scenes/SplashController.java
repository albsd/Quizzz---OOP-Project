package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.Game;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private TextField nickField;

    @FXML
    private Label warning, title;
    
    @FXML
    private Parent popup;

    @FXML
    private PopupController popupController;

    @FXML
    private Button singleplayerButton, leaderBoardButton, multiplayerButton;

    private final Font font1, font2;
    public final Color red = new Color(0.8, 0, 0, 1);
    public final Color green = new Color(0, 0.6, 0, 1);

    private final ServerUtils server;

    private final FXMLController fxml;

    @Inject
    public SplashController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.font1 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
        this.font2 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 150);
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        title.setFont(font2);
        warning.setFont(font1);
        nickField.setFont(font1);
        singleplayerButton.setFont(font1);
        leaderBoardButton.setFont(font1);
        multiplayerButton.setFont(font1);
    }

    @FXML
    public void help(final ActionEvent event) {
        fxml.showHelp();
    }

    @FXML
    public void exitApp(final ActionEvent event) {
        popupController.open("app", () -> {
            Platform.exit();
        });
    }

    private boolean validateNickname(final String user) {
        final int maxChrLimit = 12;
        final int minChrLimit = 3;
        int len = user.length();

        if (len < minChrLimit || len > maxChrLimit) {
            warning.setTextFill(red);
            warning.setText("Nickname should be between 3 and 12 characters");
            return false;
        }

        if (!user.matches("[a-zA-Z0-9]*")) {
            warning.setTextFill(red);
            warning.setText("Nickname can only contain letters and numbers");
            return false;
        }

        if (user.matches("[0-9]*")) {
            warning.setTextFill(red);
            warning.setText("Nickname must contain at least one letter");
            return false;
        }

        warning.setTextFill(green);
        warning.setText("Nickname set");
        return true;
    }

    @FXML
    public void singleGame(final ActionEvent event) {
        String nick = nickField.getText();
        if (!validateNickname(nick)) {
            return;
        }

        Game single = server.startSinglePlayer(nick);
        fxml.showSinglePlayer(single);
    }

    /**
     * Enter the lobby from the splash screen
     * The Player's nickname must be validated against the length constraints and
     * further against the names of the current players in the lobby.
     * 
     * @param event
     */
    @FXML
    public void lobby(final ActionEvent event) {
        String nick = nickField.getText();
        if (!validateNickname(nick)) {
            return;
        }

        final Player me = server.joinGame(nick);
        if (me == null) {
            warning.setTextFill(red);
            warning.setText("User with the given name is already in the game");
            return;
        }
        fxml.showLobby(me);
    }

    @FXML
    public void leaderBoard(final ActionEvent event) {
        fxml.showLeaderboard(server.getSinglePlayerLeaderboard());
    }
}
