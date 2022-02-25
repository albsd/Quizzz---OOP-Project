package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


import java.io.IOException;

public class LeaderboardController {

    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;


    @FXML
    protected void onConfirmButtonClick(ActionEvent e) throws IOException {
        root = FXMLLoader.load(LeaderboardApplication.class.getResource("placeholder-view.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onReturnButtonClick(ActionEvent e) throws IOException {
        root = FXMLLoader.load(LeaderboardApplication.class.getResource("leaderboard-leave-confirmation-view.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void switchToLeaderboard(ActionEvent e) throws IOException {
        root = FXMLLoader.load(LeaderboardApplication.class.getResource("leaderboard-view.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
