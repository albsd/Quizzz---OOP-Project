package client.scenes;

import client.utils.ServerUtils;
import commons.Game;
import commons.Leaderboard;
import commons.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import client.FXMLController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LeaderboardController implements Initializable {

    public final Color orange = new Color(1, 0.84, 0.26, 1); // #ffd644
    public final Color grey = new Color(0.918, 0.914, 0.914, 1); // #eae9e9
    public final Color green = new Color(0.878, 0.988, 0.812, 1); // e0fccf
    public final Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);

    @FXML
    private Label title, rank, nick, score;

    @FXML
    private VBox menu, playerRanking;

    @FXML
    private Button lobby, singleplayer;

    @FXML
    private ImageView backButton;

    private Player prevMe;

    private final FXMLController fxml;
    
    private final ServerUtils server;

    @Inject
    public LeaderboardController(final ServerUtils server, final FXMLController fxml) {
        this.fxml = fxml;
        this.server = server;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 72);
        title.setFont(titleFont);
        rank.setFont(font);
        nick.setFont(font);
        score.setFont(font);
        menu.setVisible(false);
    }

    @FXML
    public void backToSplash(final ActionEvent e) {
        fxml.showSplash();
    }

    public void show() {
        menu.setVisible(true);
    }

    public void hide() {
        menu.setVisible(false);
    }

    public void endGame(final Player me) {
        lobby.setFont(font);
        lobby.setVisible(true);
        singleplayer.setFont(font);
        singleplayer.setVisible(true);
        this.prevMe = me;
    }

    @FXML
    public void lobby(final ActionEvent event) {
        final Player me = server.joinLobby(prevMe.getNick());
        fxml.showLobby(me);
    }

    @FXML
    public void singleplayer() {
        Game single = server.startSinglePlayer(prevMe.getNick());
        fxml.showSinglePlayer(single);
    }

    public void displayLeaderboard(final Leaderboard leaderboard, final Player me) {
        nick.setText(me.getNick());
        score.setText(Integer.toString(me.getScore()));
        playerRanking.getChildren().clear();
        List<Player> ranking = leaderboard.getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            Player player = ranking.get(i);

            Label rankLabel = new Label("#" + (i + 1));
            Label nickLabel = new Label(player.getNick());
            Label scoreLabel = new Label(Integer.toString(player.getScore()));
            rankLabel.setFont(font);
            nickLabel.setFont(font);
            scoreLabel.setFont(font);

            StackPane entry = new StackPane();
            StackPane.setAlignment(rankLabel, Pos.CENTER_LEFT);
            StackPane.setAlignment(nickLabel, Pos.CENTER);
            StackPane.setAlignment(scoreLabel, Pos.CENTER_RIGHT);

            entry.setPadding(new Insets(10, 10, 10, 10));
            entry.getChildren().addAll(rankLabel, nickLabel, scoreLabel);

            if (i == 0) {
                entry.setBackground(new Background(new BackgroundFill(orange,
                        new CornerRadii(0.0), new Insets(0, 0, 0, 0))));
            } else {
                entry.setBackground(new Background(new BackgroundFill(grey,
                        new CornerRadii(0.0), new Insets(0, 0, 0, 0))));
            }
            if (player.getNick().equals(me.getNick())) {
                rank.setText("#" + (i + 1));
                entry.setBackground(new Background(new BackgroundFill(green,
                        new CornerRadii(0.0), new Insets(0, 0, 0, 0))));
            }

            playerRanking.getChildren().add(entry);
        }
    }

    @FXML
    public void hideBackButton() {
        backButton.setVisible(false);
    }
}
