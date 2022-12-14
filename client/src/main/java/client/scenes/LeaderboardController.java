package client.scenes;

import client.sounds.Sound;
import client.sounds.SoundName;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

public class LeaderboardController implements Initializable {

    @FXML
    private Label title, rank, nick, score;

    @FXML
    private VBox menu, playerRanking;

    @FXML
    private Button lobby, singleplayer, backButton;

    @FXML
    private ProgressBar selfScore;

    @FXML
    private StackPane leaderboardPlaque;

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
        menu.setVisible(false);
    }

    @FXML
    public void backToSplash(final ActionEvent e) {
        new Sound(SoundName.click).play(false, false);
        fxml.showSplash();
    }

    public void show() {
        menu.setVisible(true);
    }

    public void hide() {
        menu.setVisible(false);
    }

    public void endGame(final Player me) {
        lobby.setVisible(true);
        singleplayer.setVisible(true);
        this.prevMe = me;
    }

    @FXML
    public void lobby(final ActionEvent event) {
        new Sound(SoundName.pop).play(false, false);
        final Player me = server.joinLobby(prevMe.getNick());
        fxml.showLobby(me);
    }

    @FXML
    public void singleplayer() {
        new Sound(SoundName.pop).play(false, false);
        Game single = server.startSinglePlayer(prevMe.getNick());
        fxml.showSinglePlayer(single);
    }

    public void displayLeaderboard(final Leaderboard leaderboard, final Player me) {
        if (me.getNick() == null) {
            leaderboardPlaque.setVisible(false);
        }
        nick.setText(me.getNick());
        playerRanking.getChildren().clear();
        List<Player> ranking = leaderboard.getRanking();
        if (ranking.size() == 0) return;

        int maxScore = ranking.get(0).getScore();
        Boolean plaqueSet = false;
        for (int i = 0; i < ranking.size(); i++) {
            Player player = ranking.get(i);

            Label rankLabel = new Label("#" + (i + 1));
            rankLabel.setPadding(new Insets(0, 0, 0, 10));
            Label nickLabel = new Label(player.getNick());
            Label scoreLabel = new Label(Integer.toString(player.getScore()));
            scoreLabel.setPadding(new Insets(0, 10, 0, 0));

            StackPane entry = new StackPane();
            StackPane.setAlignment(rankLabel, Pos.CENTER_LEFT);
            StackPane.setAlignment(nickLabel, Pos.CENTER);
            StackPane.setAlignment(scoreLabel, Pos.CENTER_RIGHT);

            ProgressBar progressBar = new ProgressBar(player.getScore() / (double) maxScore);
            progressBar.setPrefHeight(55.2);
            progressBar.setPrefWidth(600);

            entry.getChildren().add(progressBar);
            entry.getChildren().addAll(rankLabel, nickLabel, scoreLabel);

            if (player.getNick().equals(me.getNick()) && !plaqueSet) {
                plaqueSet = true;
                rank.setText("#" + (i + 1));
                score.setText(Integer.toString(player.getScore()));
                selfScore.setProgress(player.getScore() / (double) maxScore);
                entry.getStyleClass().add("leaderboardSelf");
            } else if (i == 0) {
                entry.getStyleClass().add("leaderboardFirst");
            } else {
                entry.getStyleClass().add("leaderboardEntry");
            }

            playerRanking.getChildren().add(entry);
        }
    }

    public void hideBackButton() {
        backButton.setVisible(false);
    }
}
