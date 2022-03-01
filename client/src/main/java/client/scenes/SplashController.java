package client.scenes;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
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

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warning.setTextAlignment(TextAlignment.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        exitLabel.setTextAlignment(TextAlignment.CENTER);
    }

    public void help(ActionEvent e) throws IOException {
        //When we have the help.fxml and helpController class

//        FXMLLoader loader = new FXMLLoader(SplashController.class.getResource("Help.fxml"));
//        root = loader.load();
//        HelpController helpController = loader.getController();
//
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void exitApp(ActionEvent e) {
        Platform.exit();
    }


    public boolean checkNicknameLength(String user) {
        int maxChrLimit = 6;
        int minChrLimit = 3;
        int userLength = user.length();
        if (userLength > maxChrLimit || userLength < minChrLimit) {
            return false;
        }
        return true;
    }

    public void singleGame(ActionEvent e) throws IOException {
        String user = userField.getText();
        if(!checkNicknameLength(user)){
            warning.setText("Nickname should be min 3, max 6 characters");
        }
        else{
            warning.setText("Nickname set");
        }

//        FXMLLoader loader = new FXMLLoader(SplashController.class.getResource("Single.fxml"));
//        root = loader.load();
//        SingleController singleController = loader.getController();
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void lobby(ActionEvent e) {
        String user = userField.getText();
        if(!checkNicknameLength(user)){
            warning.setText("Nickname should be min 3, max 6 characters");
        }
        else{
            warning.setText("Nickname set");
        }
//        FXMLLoader loader = new FXMLLoader(SplashController.class.getResource("Single.fxml"));
//        String user = userField.getText();
//        root = loader.load();
//        multiController multiController = loader.getController();
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    public void leaderBoard(ActionEvent e) {
        //When we have the leaderboard.fxml and leaderboardController class

//        FXMLLoader loader = new FXMLLoader(SplashController.class.getResource("learderboard.fxml"));
//        root = loader.load();
//        LeaderboardController leaderController = loader.getController();
//
//        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }
}
