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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameMultiplayerController implements Initializable {

    @FXML
    private Button option1, option2, option3, emoji1, emoji2, emoji3, emoji4, timeButton, scoreButton, removeButton;
    private Label question, questionNumber;


    private Stage stage;
    private Scene scene;
    private Parent root;

    private final int font1Size = 24;
    private final int font2Size = 30;
    private final int font3Size = 72;

    public void returnMenu(final ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Splash.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Font font1 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), font1Size);
        Font font2 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), font2Size);
        Font font3 = Font.loadFont(getClass().getResourceAsStream(
                "/fonts/Righteous-Regular.ttf"), font3Size);
    }
}
