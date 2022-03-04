package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {

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

    private final List<Player> players;

    private final ServerUtils server;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        this.players = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server.registerForMessages("/topic/game_join", Player.class, p -> {
            players.add(p);
        });
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

    public void start(final ActionEvent event) {
        // TODO: display the multiplayer fxml
    }

}
