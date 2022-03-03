package client.scenes;

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
public class SplashController {
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
    

    public void help(final ActionEvent e) throws IOException {
        // When we have the help.fxml and helpController class

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Help.fxml"));
        root = loader.load();
        HelpController helpController = loader.getController();

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exitApp(final ActionEvent e) {
        Platform.exit();
    }

    public boolean checkNicknameLength(final String user) {
        final int maxChrLimit = 8;
        final int minChrLimit = 3;
        int userLength = user.length();
        return (userLength <= maxChrLimit && userLength >= minChrLimit);
    }

    public void singleGame(final ActionEvent e) throws IOException {
        String user = userField.getText();
        if (!checkNicknameLength(user)) {
            warning.setText("Nickname should be min 3, max 8 characters");
        } else {
            warning.setText("Nickname set");
        }

//        FXMLLoader loader = new FXMLLoader(S
//                plashController.class.getResource("Single.fxml"));
//        root = loader.load();
//        SingleController singleController = loader.getController();
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void lobby(final ActionEvent e) {
//        FXMLLoader loader = new FXMLLoader(
//                SplashController.class.getResource("Single.fxml"));
//        String user = userField.getText();
//        root = loader.load();
//        multiController multiController = loader.getController();
//
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void leaderBoard(final ActionEvent e) {
        //When we have the leaderboard.fxml and leaderboardController class

//        FXMLLoader loader = new FXMLLoader(
//                SplashController.class.getResource("learderboard.fxml"));
//        root = loader.load();
//        LeaderboardController leaderController = loader.getController();
//
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }
}
