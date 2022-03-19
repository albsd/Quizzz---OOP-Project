package client.scenes;

import client.utils.ServerUtils;
import commons.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import client.FXMLController;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LeaderboardController {

    public final Color orange = new Color(1, 0.84, 0.26, 1); // #ffd644
    public final Color grey = new Color(0.918, 0.914, 0.914, 1); // #eae9e9
    public final Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);

    @FXML
    private VBox playerRanking;

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
        List<Player> ranking = server.getLeaderboard(id.toString()).getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            StackPane entry = new StackPane();
            if (i == 0) {
                entry.setBackground(new Background(new BackgroundFill(orange,
                        new CornerRadii(0.0), new Insets(0, 0, 0, 0))));
            } else {
                entry.setBackground(new Background(new BackgroundFill(grey,
                        new CornerRadii(0.0), new Insets(0, 0, 0, 0))));
            }

            Player player = ranking.get(i);
            Label rankLabel = new Label("#" + (i + 1));
            Label nickLabel = new Label(player.getNick());
            Label scoreLabel = new Label(Integer.toString(player.getScore()));
            rankLabel.setFont(font);
            nickLabel.setFont(font);
            scoreLabel.setFont(font);
            StackPane.setAlignment(rankLabel, Pos.CENTER_LEFT);
            StackPane.setAlignment(nickLabel, Pos.CENTER);
            StackPane.setAlignment(scoreLabel, Pos.CENTER_RIGHT);

            entry.setPadding(new Insets(10, 10, 10, 10));
            entry.getChildren().addAll(rankLabel, nickLabel, scoreLabel);

            playerRanking.getChildren().add(entry);
        }
    }
}
