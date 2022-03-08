package client.scenes;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class LeaderboardController {

    @FXML
    private Stage stage;
    @FXML
    private Scene scene;

    @FXML
    protected void onConfirmButtonClick(final ActionEvent e) {
        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onReturnButtonClick(final ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation Screen");
        alert.setHeaderText("Confirmation needed!");
        alert.setContentText(
                "You are about to leave to the main screen. Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            onConfirmButtonClick(e);
        } else {
            switchToLeaderboard(e);
        }
    }

    @FXML
    public void switchToLeaderboard(final ActionEvent e) {
        var root = Main.FXML.load(LeaderboardController.class, "client", "scenes", "Leaderboard.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }
}
