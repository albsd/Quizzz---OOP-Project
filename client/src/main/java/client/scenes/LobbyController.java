package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class LobbyController implements Initializable {

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

    private final List<Player> players = new ArrayList<>();

    private final ServerUtils server;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        server.registerForMessages("game/topic/game_join", Player.class, gameJoinConsumer);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        List<Player> lobbyPlayers = server.getPlayers();
        if (lobbyPlayers != null) {
            players.addAll(lobbyPlayers);
            for (Player p : lobbyPlayers) {
                gameJoinConsumer.accept(p);
            }
        }
    }

    private Consumer<Player> gameJoinConsumer = p -> {
        System.out.println("Player " + p.getNick() + " joined");
        players.add(p);
        String text = playersLeft.getText();
        playersLeft.setText(text + "\n" + p.getNick());
    };

    public void addPlayer(final Player p) {
        gameJoinConsumer.accept(p);
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
        // server.startGame();
    }

}
