package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.JoinMessage;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;

public class SplashController {

    public final Color red = new Color(0.8, 0, 0, 1);
    public final Color green = new Color(0, 0.6, 0, 1);

    @Autowired
    private final ServerUtils server;

    @FXML
    private TextField nickField;

    @FXML
    private Label warning;

    @FXML
    private Label title;

    @FXML
    private Label exitLabel;

    private Stage stage;

    private Scene scene;

    @Inject
    public SplashController(final ServerUtils server) {
        this.server = server;
    }

    public void help(final ActionEvent event) {
        var root = Main.FXML.load(HelpController.class, "client", "scenes", "Help.fxml");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    public void exitApp(final ActionEvent event) {
        Platform.exit();
    }

    public boolean validateNicknameLength(final String user) {
        final int maxChrLimit = 8;
        final int minChrLimit = 3;
        int len = user.length();

        if (len < minChrLimit || len > maxChrLimit) {
            warning.setTextFill(red);
            warning.setText("Nickname should be between 3 and 8 characters");
            return false;
        }

        warning.setTextFill(green);
        warning.setText("Nickname set");
        return true;
    }

    public void singleGame(final ActionEvent event) {
        String user = nickField.getText();
        if (!validateNicknameLength(user)) {
            return;
        }
        // TODO: load the fxml and display it
    }

    /**
     * Enter the lobby from the splash screen
     * The Player's nickname must be validated against the length constraints and
     * further against the names of the current players in the lobby.
     * 
     * @param event
     */
    public void lobby(final ActionEvent event) {
        var root = Main.FXML.load(
                LobbyController.class, "client", "scenes", "Lobby.fxml");
        var ctrl = root.getKey();

        String nick = nickField.getText();
        if (!validateNicknameLength(nick)) {
            return;
        }

        final Player player = server.joinGame(nick);
        if (player == null) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("User with the given name is already in the game");
            alert.showAndWait();
            return;
        }

        ctrl.setMe(player);
        server.send("/app/join", new JoinMessage(player, true));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();

    }

    public void leaderBoard(final ActionEvent event) {
        var root = Main.FXML.load(
                LeaderboardController.class, "client", "scenes", "Leaderboard.fxml");

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }
}
