package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import javax.inject.Inject;

import client.FXMLController;

public class LeaderboardController {

    private final FXMLController fxml;

    @Inject
    public LeaderboardController(final FXMLController fxml) {
        this.fxml = fxml;
    }

    @FXML
    protected void onConfirmButtonClick(final ActionEvent e) {
        fxml.showSplash();
    }

    @FXML
    protected void onReturnButtonClick(final ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation Screen");
        alert.setHeaderText("Confirmation needed!");
        alert.setContentText("You are about to leave to the main screen. Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            onConfirmButtonClick(e);
        } else {
            switchToLeaderboard(e);
        }
    }

    @FXML
    public void switchToLeaderboard(final ActionEvent e) {
        fxml.showLeaderboard();
    }
}
