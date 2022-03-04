package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LobbyController {

    @FXML
    private TextField userField;

    @FXML
    private Label chatText;

    @FXML
    private TextField chatInput;

    @FXML
    private Label playersLeft;

    @FXML
    private Label playersRight;

    private Stage stage;

    private Scene scene;

    private final List<Player> players;

    @Autowired
    private final ServerUtils server;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        this.players = new ArrayList<>();
        server.registerForMessages("/topic/game_join", Player.class, p -> {
            players.add(p);
        });
    }

    public void returnMenu(final ActionEvent e) throws IOException {
        var root = Main.FXML.load(SplashController.class,
                "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    public void start(final ActionEvent event) {
        // TODO: display the multiplayer fxml
    }

}
