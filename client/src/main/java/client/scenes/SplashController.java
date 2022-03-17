package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.Game;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import javax.inject.Inject;

public class SplashController {

    @FXML
    private TextField nickField;

    @FXML
    private Label warning;

    @FXML
    private Label title;

    public final Color red = new Color(0.8, 0, 0, 1);
    public final Color green = new Color(0, 0.6, 0, 1);

    private final ServerUtils server;

    private final FXMLController fxml;

    @Inject
    public SplashController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
    }

    @FXML
    public void help(final ActionEvent event) {
        fxml.showHelp();
    }

    @FXML
    public void exitApp(final ActionEvent event) {
        Platform.exit();
    }

    private boolean validateNickname(final String user) {
        final int maxChrLimit = 8;
        final int minChrLimit = 3;
        int len = user.length();

        if (len < minChrLimit || len > maxChrLimit) {
            warning.setTextFill(red);
            warning.setText("Nickname should be between 3 and 8 characters");
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
        fxml.showLeaderboard();
    }
}
