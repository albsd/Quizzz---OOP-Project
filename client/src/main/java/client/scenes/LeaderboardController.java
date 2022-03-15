package client.scenes;



import client.utils.ServerUtils;
import com.google.inject.Inject;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;



import client.FXMLController;

public class LeaderboardController {
    private int questionNumber = 0;
    private ServerUtils server;
    @FXML
    private ListView playerRanking;


    private FXMLController fxml;

    @Inject
    public LeaderboardController(final FXMLController fxml, ServerUtils server) {
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

    public void setQuestionNumber(final int questionNumber) {
        this.questionNumber = questionNumber;
        System.out.println(questionNumber);
    }

    public void displayLeaderboard(final String id) {
        //TODO:Uncomment this when game initialization functionality has been implemented
//        Leaderboard leaderboard = server.getLeaderboard(id);
//        System.out.println(Arrays.toString(leaderboard.getRanking().toArray()));
        //I am using a "dummy leaderboard object to test displaying the leaderboard"
        Leaderboard leaderboard = new Leaderboard();
        Player player1 = new Player("Shaq", 0);
        Player player2 = new Player("Lolo", 0);
        Player player3 = new Player("Lohithsai Yadala Chanchu", 2);

        leaderboard.setRanking(Arrays.asList(player1, player2, player3));

        List<String> names = new ArrayList<>();
        for (Player player:leaderboard.getRanking()) {
            //playerRanking.getItems().add(this.calculateBuffer(player.getNick(), player.getScore()));
            names.add(this.calculateBuffer(player.getNick(), player.getScore()));
        }
        ObservableList<String> items = FXCollections.observableArrayList(names);
        playerRanking.setItems(items);
        

    }
    //TODO:Make scores align. This should make it align but listView is weird so maybe we can change leaderboard fxml in another issue?
    public String calculateBuffer(final String nick, final int score) {
        String buffer = "";
        int scorePosition = 80;
        for (int i = 0; i < scorePosition - nick.length(); i++) {
            buffer += " ";
        }
        System.out.println(nick + buffer + score);
        return nick + buffer + score;
    }
}
