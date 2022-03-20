package client.scenes;

import client.utils.ServerUtils;
import commons.Leaderboard;
import commons.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import client.FXMLController;

public class LeaderboardController {

    @FXML
    private ListView<String> playerRanking;

    private final ServerUtils server;

    private final FXMLController fxml;

    @Inject
    public LeaderboardController(final ServerUtils server, final FXMLController fxml) {
        this.fxml = fxml;
        this.server = server;
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

    public void displayLeaderboard(final UUID id) {
        Leaderboard leaderboard = server.getLeaderboard(id);
        List<String> names = new ArrayList<>();
        for (Player player : leaderboard.getRanking()) {
            names.add(calculateBuffer(player.getNick(), player.getScore()));
        }
        ObservableList<String> items = FXCollections.observableArrayList(names);
        playerRanking.setItems(items);
    }

    public void displayLeaderboard(final Leaderboard singlePlayerLeaderboard) {
        List<String> names = new ArrayList<>();
        for (Player player : singlePlayerLeaderboard.getRanking()) {
            names.add(calculateBuffer(player.getNick(), player.getScore()));
        }
        ObservableList<String> items = FXCollections.observableArrayList(names);
        playerRanking.setItems(items);
    }

    // TODO: Make scores align. This should make it align but listView is weird so
    // maybe we can change leaderboard fxml in another issue?
    private String calculateBuffer(final String nick, final int score) {
        String buffer = "";
        int scorePosition = 80;
        for (int i = 0; i < scorePosition - nick.length(); i++) {
            buffer += " ";
        }
        return nick + buffer + score;
    }
}
