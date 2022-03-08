package client.scenes;

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
public class LobbyController {
    @FXML
    private TextField userField;
    @FXML
    private Label chatText;
    @FXML
    private Label playersLeft;
    @FXML
    private Label playersRight;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void returnMenu(final ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Splash.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void start(final ActionEvent e) throws IOException {
        //Starting the game

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("GameMultiplayer.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
