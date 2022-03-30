package client.scenes;

import client.FXMLController;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SplashController implements Initializable {

    @FXML
    private TextField nickField;

    @FXML
    private Label warning;
    
    @FXML
    private PopupController popupController;

    private final ServerUtils server;

    private final FXMLController fxml;

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

        try {
            Scanner sc = new Scanner(new File("./src/main/resources/nick.txt"));
            nickField.setText(sc.nextLine());
            sc.close();
        } catch (FileNotFoundException e) {
            //System.out.println("No nickname set.");
        }
    }

    @FXML
    public void help(final ActionEvent event) {
        fxml.showHelp();
    }

    @FXML
    public void theme(final ActionEvent event) {
        fxml.showThemeSelector();
    }

    @FXML
    public void exitApp(final ActionEvent event) {
        popupController.open("app", () -> {
            System.exit(0);
        });
    }

    private boolean validateNickname(final String user) {
        final int maxChrLimit = 12;
        final int minChrLimit = 3;
        int len = user.length();

        if (len < minChrLimit || len > maxChrLimit) {
            warning.getStyleClass().add("incorrectText");
            warning.setText("Nickname should be between 3 and 12 characters");
            return false;
        }

        if (!user.matches("[a-zA-Z0-9]*")) {
            warning.getStyleClass().add("incorrectText");
            warning.setText("Nickname can only contain letters and numbers");
            return false;
        }

        if (user.matches("[0-9]*")) {
            warning.getStyleClass().add("incorrectText");
            warning.setText("Nickname must contain at least one letter");
            return false;
        }

        warning.getStyleClass().add("correctText");
        warning.setText("Nickname set");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/nick.txt"));
            writer.write(user);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @FXML
    public void singleGame(final ActionEvent event) {
        String nick = nickField.getText();
        nickField.setText(nick);
        if (!validateNickname(nick)) {
            return;
        }
        Game singleGame = server.startSinglePlayer(nick);
        fxml.showSinglePlayer(singleGame);
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
        nickField.setText(nick);
        if (!validateNickname(nick)) {
            return;
        }

        final Player me = server.joinLobby(nick);
        if (me == null) {
            warning.getStyleClass().add("correctText");
            warning.setText("User with the given name is already in the game");
            return;
        }
        fxml.showLobby(me);
    }

    @FXML
    public void leaderBoard(final ActionEvent event) {
        fxml.showLeaderboard(server.getSinglePlayerLeaderboard());
    }

    @FXML
    public void admin(final ActionEvent event) {
        fxml.showAdminPanel();
    }
}
