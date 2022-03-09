package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.JoinMessage;
import commons.LobbyMessage;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.web.util.HtmlUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class LobbyController implements Initializable {

    @FXML
    private ScrollPane chatArea;

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

    private List<Player> players;

    private final ServerUtils server;

    private Player me;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        this.players = new ArrayList<>();
        server.registerForMessages("/topic/join", JoinMessage.class, playerConsumer);
        server.registerForMessages("/topic/lobby/chat",
                LobbyMessage.class, messageConsumer);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.players = new ArrayList<>();
        List<Player> lobbyPlayers = server.getPlayers();
        if (lobbyPlayers != null) {
            for (Player p : lobbyPlayers) {
                playerConsumer.accept(new JoinMessage(p, true));
            }
        }
    }

    @FXML
    public void onEnter(final ActionEvent e) {
        String content = chatInput.getText();
        chatInput.setText("");
        final int demoTime = 10;
        // escapes special characters in input
        server.send("/app/lobby/chat",
                new LobbyMessage(me, demoTime, HtmlUtils.htmlEscape(content)));
        chatArea.setVvalue(1.0);
    }

    public void setMe(final Player me) {
        this.me = me;
    }

    private final Consumer<JoinMessage> playerConsumer = msg -> {
        final Player wsPlayer = msg.getPlayer();
        if (msg.isJoining()) {
            players.add(wsPlayer);
        } else {
            players.remove(wsPlayer);
        }

        // GUI Updates must be run later
        // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerCount.setText("Number of players: " + players.size());

                if (msg.isJoining()) {
                    final Label column = left ? playersLeft : playersRight;
                    left = !left;
                    String colText = column.getText();
                    String newText = colText + wsPlayer.getNick();
                    if (wsPlayer.getNick().equals(me.getNick())) {
                        newText += "(me)";
                    }
                    column.setText(newText + "\n\n");

                } else {
                    playersLeft.setText("");
                    playersRight.setText("");
                    left = true;

                    for (Player p : players) {
                        final Label column = left ? playersLeft : playersRight;
                        left = !left;
                        String colText = column.getText();

                        String newText = colText + p.getNick();
                        if (p.getNick().equals(me.getNick())) {
                            newText += "(me)";
                        }
                        column.setText(newText + "\n\n");
                    }
                }
            }
        });
    };

    private Consumer<LobbyMessage> messageConsumer = m -> {
        System.out.println("Message received");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String nick = m.getPlayer().getNick();
                int time = m.getTimestamp();
                String content = m.getContent();
                // change. Scroll pane is not place to put messages
                String chatLogs = chatText.getText()
                        + nick + " (" + time + ") - " + content + "\n";
                chatText.setText(chatLogs);
            }
        });
    };

    @FXML
    protected void onReturnButtonClick(final ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation Screen");
        alert.setHeaderText("Confirmation needed!");
        alert.setContentText("You are about to leave the lobby. Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            returnToMenu(event);
        }
    }

    public void returnToMenu(final ActionEvent event) {
        server.leaveGame(me.getNick());
        server.send("/app/join", new JoinMessage(me, false));

        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    public void start(final ActionEvent event) {
        // server.startGame();
        var root = Main.FXML.load(GameMultiplayerController.class, "client", "scenes", "GameMultiplayer.fxml");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }
}
