package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

import client.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

public class GameMultiplayerController implements Initializable {

    @FXML
    private Button option1, option2, option3,
            emoji1, emoji2, emoji3, emoji4,
            timeButton, scoreButton, removeButton,
            cancelButton, confirmButton;
    @FXML
    private Label question, questionNumber, points, popupText, timer1, timer2;

    @FXML
    private Pane popupMenu;

    private Stage stage;

    private Scene scene;

    private final ServerUtils server;

    private Player me;
    private Game currentGame;

    @Inject
    public GameMultiplayerController(final ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Font font1 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), 24);

        option1.setFont(font1);
        option2.setFont(font1);
        option3.setFont(font1);

        cancelButton.setFont(font1);
        confirmButton.setFont(font1);
        popupText.setFont(font1);

        question.setFont(font1);
        questionNumber.setFont(font1);
        points.setFont(font1);
        timer1.setFont(font1);
        timer2.setFont(font1);
    }

    public void returnMenu(final ActionEvent e) throws IOException {
        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    //this is for multiple choice questions
    public void checkMulChoiceAnswer(final ActionEvent e) {
        int correctAnswer = currentGame.getCurrentQuestion().getAnswer();
        String optionStr = ((Button)e.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        if (option == correctAnswer) {
            calculateMulChoicePoints();
            System.out.println("Correct answer!");
        }else {
            System.out.println("Wrong answer. No points");
        }
    }
    //this is for open questions
    public void checkOpenAnswer(final ActionEvent e) {
        int correctAnswer = currentGame.getCurrentQuestion().getAnswer();
        String optionStr = ((Button)e.getSource()).getText();
        int option = Integer.parseInt(optionStr);
        calculateOpenPoints(correctAnswer, option);
    }

    private void calculateMulChoicePoints(){
        int base = 50;
        int bonusScore = (me.getTime() / 1000) * 2;
        me.setScore(base + bonusScore);
    }

    private void calculateOpenPoints(int answer, int option) {
        int bonusScore = (me.getTime() / 1000) * 2;
        int base;
        int offPercentage = (int)Math.round(((double)Math.abs((option - answer)) / answer) * 100);
        int accuracyPercentage = 100 - offPercentage;
        if(accuracyPercentage==100) base = 100;
        else if(90<=accuracyPercentage && accuracyPercentage< 99) base=90;
        else if(80<=accuracyPercentage && accuracyPercentage< 89) base=80;
        else if(70<=accuracyPercentage && accuracyPercentage< 79) base=70;
        else if(60<=accuracyPercentage && accuracyPercentage< 69) base=60;
        else if(50<=accuracyPercentage && accuracyPercentage< 59) base=50;
        else base = 0;
        me.setScore(base + bonusScore);
    }

    //request for leaderboard every 5 questions
    public void getLeaderboard(ActionEvent e) {
        int questionNum = Integer.parseInt(questionNumber.getText());
        if (questionNum % 5 == 0) {
            Leaderboard leaderboard = server.getLeaderboard(currentGame.getId().toString());
            var root = Main.FXML.load(
                    LeaderboardController.class, "client", "scenes", "Leaderboard.fxml");

            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root.getValue());
        }
    }

    public void setMe(Player me){
        this.me = me;
    }

    public void setGame(Game game) {
        this.currentGame = game;
    }

    public void openPopup(final ActionEvent e) throws IOException {
        popupMenu.setVisible(true);
    }

    public void closePopup(final ActionEvent e) throws IOException {
        popupMenu.setVisible(false);
    }

    public void timePowerup(final ActionEvent e) {

    }

    public void scorePowerup(final ActionEvent e) {

    }

    public void removePowerup(final ActionEvent e) {

    }

}
