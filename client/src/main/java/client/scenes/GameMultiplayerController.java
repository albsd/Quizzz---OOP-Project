package client.scenes;

import commons.Game;
import commons.LobbyMessage;
import commons.Player;
import commons.Question;
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

    //find way to connect a game object and player object so that
    //we can calculate score of game timer and assign to player
    //later score must be sent to server to display leaderboard
    private Player me;
    private Game currentGame;

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

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    //this is for multiple choice questions
    public void checkAnswer(final ActionEvent e) {
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

    private void calculateMulChoicePoints(){
        int base = 50;
        int bonusScore = (me.getTime() / 1000) * 2;
        me.setScore(base + bonusScore);
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
