package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    private boolean left = true;

    @FXML
    private Label playerCount;

    private Stage stage;

    private Scene scene;

    private final List<Player> players = new ArrayList<>();

    private final ServerUtils server;

    private Player player;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        server.registerForMessages("/topic/join", Player.class, playerConsumerJoin);

        server.registerForMessages("/topic/leave", Player.class, playerConsumerLeave);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        List<Player> lobbyPlayers = server.getPlayers();
        if (lobbyPlayers != null) {
            for (Player p : lobbyPlayers) {
                playerConsumerJoin.accept(p);
            }
        }
    }

    private Consumer<Player> playerConsumerJoin = p -> {
        player = p;
        System.out.println("Player " + p.getNick() + " joined");
        players.add(p);

        // GUI Updates must be run later
        // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final Label column = left ? playersLeft : playersRight;
                left = !left;
                String colText = column.getText();
                column.setText(colText + "\n\n" + p.getNick());

                String countText = playerCount.getText();
                String[] parts = countText.split(":");
                playerCount.setText(parts[0] + ": " + players.size());
            }
        });
    };

    private Consumer<Player> playerConsumerLeave = p -> {
        player = p;
        String playersLeftString = playersLeft.toString();
        System.out.println("Player " + p.getNick() + " left");
        System.out.println(players);
        System.out.println(players.indexOf(p));
        int index = players.indexOf(p);
        if(index % 2 == 0){
            System.out.println("left column" + " row: " + index/2);

        }
        else{
            System.out.println("right column"+ "row: " + index/2);
        }
        players.remove(p);

        String[] tokens=playersLeftString.split("\n\n");
        List<String> leftColPlayers = Arrays.asList(tokens);
//        System.out.println(leftColPlayers.get(1));
//        System.out.println(p.getNick());
//        System.out.println(leftColPlayers.contains(p.getNick()));


//        leftColPlayers.remove(p.getNick());

        // GUI Updates must be run later
        // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playersLeft.setText("");
                playersRight.setText("");

                for(int i= 0; i<players.size();i++) {
                    Player p = players.get(i);
                    final Label column = left ? playersLeft : playersRight;
                    left = !left;
                    String colText = column.getText();
                    column.setText(colText + "\n\n" + p.getNick());

                    String countText = playerCount.getText();
                    String[] parts = countText.split(":");
                    playerCount.setText(parts[0] + ": " + players.size());
                }
//                final Label column = left ? playersLeft : playersRight;
//                left = !left;
//                String colText = column.getText();
//                column.setText(colText + "\n\n" + p.getNick());
//
//                String countText = playerCount.getText();
//                String[] parts = countText.split(":");
//                playerCount.setText(parts[0] + ": " + players.size());
            }
        });
    };

    public void returnMenu(final ActionEvent e) {
        server.leaveGame(player.getNick());
        server.send("/app/leave", player);

        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");
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
