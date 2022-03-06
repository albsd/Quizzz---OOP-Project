package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.JoinMessage;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.Optional;

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

    private final List<JoinMessage> players = new ArrayList<>();

    private final ServerUtils server;

    private Player player;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
//        server.registerForMessages("/topic/join", Player.class, playerConsumerJoin);
//
//        server.registerForMessages("/topic/leave", Player.class, playerConsumerLeave);
        server.registerForMessages("/topic/joinAndLeave", JoinMessage.class, playerConsumerJoinAndLeave);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        List<Player> lobbyPlayers = server.getPlayers();
        if (lobbyPlayers != null) {
            for (Player p : lobbyPlayers) {
                playerConsumerJoinAndLeave.accept(new JoinMessage(p, true));
            }
        }
    }

    private Consumer<JoinMessage> playerConsumerJoinAndLeave = p -> {
        System.out.println("called" + p.getPlayer().getNick());
        if(p.isJoining()){
            player = p.getPlayer();
            System.out.println("Player " + player.getNick() + " joined");
            players.add(p);

            // GUI Updates must be run later
            // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    final Label column = left ? playersLeft : playersRight;
                    left = !left;
                    String colText = column.getText();
                    column.setText(colText + "\n\n" + player.getNick());

                    String countText = playerCount.getText();
                    String[] parts = countText.split(":");
                    playerCount.setText(parts[0] + ": " + players.size());
                }
            });
        }
        else {
            player = p.getPlayer();
            String playersLeftString = playersLeft.toString();
            System.out.println("Player " + player.getNick() + " left");
            players.remove(p);

            String[] tokens = playersLeftString.split("\n\n");
            List<String> leftColPlayers = Arrays.asList(tokens);

            //TODO: Fix display names of players who left after better replacement is found for label object
            // GUI Updates must be run later
            // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    playersLeft.setText("");
                    playersRight.setText("");

                    for (int i = 0; i < players.size(); i++) {
                        Player p = players.get(i).getPlayer();
                        final Label column = left ? playersLeft : playersRight;
                        left = !left;
                        String colText = column.getText();
                        column.setText(colText + "\n\n" + p.getNick());

                        String countText = playerCount.getText();
                        String[] parts = countText.split(":");
                        playerCount.setText(parts[0] + ": " + players.size());
                    }
                }
            });
        }
    };

    public void returnMenu(final ActionEvent e) {
        server.leaveGame(player.getNick());
        server.send("/app/joinAndLeave", new JoinMessage(player, false));

        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    protected void onConfirmButtonClick(final ActionEvent e) {
        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onReturnButtonClick(final ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation Screen");
        alert.setHeaderText("Confirmation needed!");
        alert.setContentText(
                "You are about to leave to the main screen. Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            returnMenu(e);
        }
    }
    @FXML
    public void switchToLobby(final ActionEvent e) {
        var root = Main.FXML.load(LeaderboardController.class, "client", "scenes", "Lobby.fxml");

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
