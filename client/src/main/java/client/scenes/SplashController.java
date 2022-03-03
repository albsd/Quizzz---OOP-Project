package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public class SplashController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField userField;
    @FXML
    private Label warning;
    @FXML
    private Label title;
    @FXML
    private Label exitLabel;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @Inject
    public SplashController(final ServerUtils server, final MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void help(final ActionEvent e) throws IOException {
        // When we have the help.fxml and helpController class

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Help.fxml"));
        root = loader.load();
        HelpController helpController = loader.getController();

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exitApp(final ActionEvent e) {
        Platform.exit();
    }

    public boolean validateNicknameLength(final String user) {
        final int maxChrLimit = 8;
        final int minChrLimit = 3;
        int userLength = user.length();
        boolean valid = userLength <= maxChrLimit && userLength >= minChrLimit;
        if (valid) {
            warning.setTextFill(mainCtrl.green);
            warning.setText("Nickname set");
        } else {
            warning.setTextFill(mainCtrl.red);
            warning.setText("Nickname should be between 3 and 8 characters");
        }
        return valid;
    }

    public void singleGame(final ActionEvent e) throws IOException {
        String user = userField.getText();
        if (!validateNicknameLength(user))
            return;

        // FXMLLoader loader = new FXMLLoader(S
        // plashController.class.getResource("Single.fxml"));
        // root = loader.load();
        // SingleController singleController = loader.getController();
        // stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        // scene = new Scene(root);
        // stage.setScene(scene);
        // stage.show();
    }

    public void lobby(final ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Lobby.fxml"));
        String user = userField.getText();
        if (!validateNicknameLength(user))
            return;

        Consumer<Player> gameJoinConsumer = player -> {
            System.out.println("Player " + player.getNick() + " joined");
        };
        server.registerForMessages("/topic/game_join", Player.class,
                gameJoinConsumer);
        server.send("/game/join", user);

        root = loader.load();
        // multiController multiController = loader.getController();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void leaderBoard(final ActionEvent e) {
        // When we have the leaderboard.fxml and leaderboardController class

        // FXMLLoader loader = new FXMLLoader(
        // SplashController.class.getResource("learderboard.fxml"));
        // root = loader.load();
        // LeaderboardController leaderController = loader.getController();
        //
        // stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        // scene = new Scene(root);
        // stage.setScene(scene);
        // stage.show();
    }
}
