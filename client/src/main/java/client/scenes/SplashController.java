package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class SplashController {

    public final Color red = new Color(0.8, 0, 0, 1);
    public final Color green = new Color(0, 0.6, 0, 1);

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

    private Parent root;

    @Inject
    public SplashController(final ServerUtils server) {
        this.server = server;
    }

    public void help(final ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Help.fxml"));

        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
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

        if (len < minChrLimit && len > maxChrLimit) {
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
        if (!validateNicknameLength(user))
            return;
        // TODO: load the fxml and display it
    }

    public void lobby(final ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Lobby.fxml"));

        String nick = nickField.getText();
        if (!validateNicknameLength(nick))
            return;

        try {
            server.joinGame(nick);
            server.send("app/game/join/" + nick, nick);

            root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException | InterruptedException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void leaderBoard(final ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("leaderboard-view.fxml"));
        root = loader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
