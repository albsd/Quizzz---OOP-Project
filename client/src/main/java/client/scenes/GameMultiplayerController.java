package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private Parent root;

    private final int font1Size = 24;
    private final int font2Size = 30;
    private final int font3Size = 72;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Font font1 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), font1Size);
        Font font2 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), font2Size);
        Font font3 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), font3Size);

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
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Splash.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
