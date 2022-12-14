package client.scenes;

import client.FXMLController;
import client.sounds.Sound;
import client.sounds.SoundName;
import client.utils.ServerUtils;
import commons.Game;
import commons.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private TextField nickField;

    @FXML
    private Label warning;

    @FXML
    private PopupController popupController;

    private final ServerUtils server;

    private final FXMLController fxml;

    private String nick;

    @Inject
    public SplashController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
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
        //Players can prefer to play with server saved nickname or new nickname
        if (nick == null) {
            String serverNick = server.getNickname();
            if (serverNick != null) {
                nick = serverNick;
                fxml.saveNick(nick);
                nickField.setText(nick);
            }
        //nick has already been set so used persistent fxml nick
        } else {
            nick = fxml.getNick();
            nickField.setText(nick);
        }
    }

    @FXML
    public void help(final ActionEvent event) {
        new Sound(SoundName.click).play(false, false);
        fxml.showHelp();
    }

    @FXML
    public void theme(final ActionEvent event) {
        new Sound(SoundName.click).play(false, false);
        fxml.showThemeSelector();
    }

    @FXML
    public void exitApp(final ActionEvent event) {
        new Sound(SoundName.click).play(false, false);
        popupController.open("Do you really want to close the app?", () -> {
            System.exit(0);
        });
    }

    private boolean validateNickname(final String user) {
        final int maxChrLimit = 12;
        final int minChrLimit = 3;
        int len = user.length();
        if (len < minChrLimit || len > maxChrLimit) {
            setWarningClass("incorrectText");
            warning.setText("Nickname should be between 3 and 12 characters");
            return false;
        }

        if (!user.matches("[a-zA-Z0-9]*")) {
            setWarningClass("incorrectText");
            warning.setText("Nickname can only contain letters and numbers");
            return false;
        }

        if (user.matches("[0-9]*")) {
            setWarningClass("incorrectText");
            warning.setText("Nickname must contain at least one letter");
            return false;
        }
        return true;
    }

    @FXML
    public void singleGame(final ActionEvent event) {
        new Sound(SoundName.pop).play(false, false);
        if (!validateNickname(nickField.getText())) {
            return;
        }
        nick = nickField.getText();
        nickField.setText(nick);
        server.saveNickname(nick);
        fxml.saveNick(nick);
        Game singleGame = server.startSinglePlayer(nick);
        fxml.showSinglePlayer(singleGame);
    }

    /**
     * Enter the lobby from the splash screen.
     * The Player's nickname must be validated against the names of the 
     * current players in the lobby.
     * @param event
     */
    @FXML
    public void lobby(final ActionEvent event) {
        new Sound(SoundName.pop).play(false, false);
        if (!validateNickname(nickField.getText())) {
            return;
        }
        nick = nickField.getText();
        final Player me = server.joinLobby(nick);
        if (me == null) {
            setWarningClass("incorrectText");
            warning.setText("User with the given name is already in the game");
            return;
        }
        server.saveNickname(nick);
        fxml.saveNick(nick);
        fxml.showLobby(me);
    }

    private void setWarningClass(final String styleClass) {
        warning.getStyleClass().removeAll("incorrectText", "correctText");
        warning.getStyleClass().add(styleClass);
    }

    @FXML
    public void leaderBoard(final ActionEvent event) {
        new Sound(SoundName.pop).play(false, false);
        if (!validateNickname(nickField.getText())) {
            return;
        }
        nick = nickField.getText();
        server.saveNickname(nick);
        fxml.saveNick(nick);
        fxml.showLeaderboard(server.getSinglePlayerLeaderboard());
    }

    @FXML
    public void admin(final ActionEvent event) {
        new Sound(SoundName.pop).play(false, false);
        fxml.showAdminPanel();
    }
}
