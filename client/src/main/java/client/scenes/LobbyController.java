package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.Game;
import commons.LobbyMessage;
import commons.Player;
import commons.PlayerUpdate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

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

    @FXML
    private Label playerCount;

    private List<String> players;

    private final ServerUtils server;

    private final FXMLController fxml;

    private Player me;

    private final DateTimeFormatter timeFormat;

    @Inject
    public LobbyController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.players = new ArrayList<>();
        this.timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss");

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // We DON'T use the shorthand .toList() here, because that returns an immutable
        // list and causes player updates to get ignored silently
        this.players = server.getPlayers().stream()
                .map(Player::getNick)
                .collect(Collectors.toList());

        updatePlayerList();

        server.registerForMessages("/topic/lobby/player", PlayerUpdate.class, update -> {
            if (update.getContent() == PlayerUpdate.Type.join) {
                players.add(update.getNick());
            } else {
                players.remove(update.getNick());
            }
            updatePlayerList();
        });

        server.registerForMessages("/topic/lobby/chat", LobbyMessage.class, message -> {
            Platform.runLater(() -> {
                String chatBox = chatText.getText() + message.toString();
                chatText.setText(chatBox);
            });
        });

        server.registerForMessages("/topic/lobby/start", Game.class, game -> {
            Platform.runLater(() -> {
                var root = fxml.showGame();
                var ctrl = root.getKey();
                ctrl.setGame(me, game);
            });
        });
    }

    @FXML
    public void onEnter(final ActionEvent e) {
        String content = chatInput.getText().replaceAll("[\"\'><&]", ""); // escape XML characters
        chatInput.setText("");

        final LocalTime time = LocalTime.now();
        final LobbyMessage message = new LobbyMessage(me.getNick(), time.format(timeFormat), content);
        
        server.send("/app/lobby/chat", message);
        chatArea.setVvalue(1.0);
    }

    public void setMe(final Player me) {
        this.me = me;
    }

    private void updatePlayerList() {
        // GUI Updates must be run later
        // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        Platform.runLater(() -> {
            playerCount.setText("Number of players: " + players.size());

            List<String> nicks = players.stream().map(nick -> {
                if (nick.equals(me.getNick())) {
                    return nick + " (me)";
                }
                return nick;
            }).toList();

            String leftText = IntStream.range(0, players.size())
                    .filter(i -> i % 2 == 0)
                    .mapToObj(nicks::get)
                    .collect(Collectors.joining("\n\n"));

            String rightText = IntStream.range(0, players.size())
                    .filter(i -> i % 2 == 1)
                    .mapToObj(nicks::get)
                    .collect(Collectors.joining("\n\n"));

            playersLeft.setText(leftText);
            playersRight.setText(rightText);
        });
    }

    @FXML
    public void onReturnButtonClick(final ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation Screen");
        alert.setHeaderText("Confirmation needed!");
        alert.setContentText("You are about to leave the lobby. Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            returnToMenu(event);
        }
    }

    @FXML
    public void returnToMenu(final ActionEvent event) {
        server.leaveGame(me.getNick());

        fxml.showSplash();
    }

    @FXML
    public void start(final ActionEvent event) {
        Game game = server.startGame();
        if (game == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
            alert.setTitle("Confirmation Screen");
            alert.setHeaderText("Confirmation needed!");
            alert.setContentText("If you want to play alone, choose SinglePlayer?");
            alert.showAndWait();
            return;
        }

        fxml.showGame();
    }
}
