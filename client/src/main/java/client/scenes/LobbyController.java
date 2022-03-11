package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.LobbyMessage;
import commons.Player;
import commons.PlayerUpdate;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private Player me;

    private final DateTimeFormatter timeFormat;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        this.players = new ArrayList<>();
        this.timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss");
        Consumer<PlayerUpdate> playerUpdateConsumer = update -> {
            System.out.println("PlayerUpdate received");
            if (update.getContent() == PlayerUpdate.Type.join) {
                players.add(update.getNick());
            } else {
                players.remove(update.getNick());
            }
            updatePlayerList();
        };
        server.registerForMessages("/topic/playerUpdate", PlayerUpdate.class, playerUpdateConsumer);

        Consumer<LobbyMessage> messageConsumer = m -> {
            System.out.println("Message received");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String nick = m.getNick();
                    //remove nanosecond when displaying time in chat
                    String time = m.getTimestamp().format(timeFormat);
                    String content = m.getContent();
                    String chatLogs = chatText.getText()
                            + nick + " (" + time + ") - " + content + "\n";
                    chatText.setText(chatLogs);
                }
            });
        };
        server.registerForMessages("/topic/lobby/chat", LobbyMessage.class, messageConsumer);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // We DON'T use the shorthand .toList() here, because that returns an immutable list and causes player updates to get ignored silently
        this.players = server.getPlayers().stream().map(Player::getNick).collect(Collectors.toList());
        updatePlayerList();
    }

    @FXML
    public void onEnter(final ActionEvent e) {
        String content = chatInput.getText();
        chatInput.setText("");
        final LocalTime time = LocalTime.now();
        // escapes special characters in input
        server.send("/app/lobby/chat",
                new LobbyMessage(me.getNick(), time, HtmlUtils.htmlEscape(content)));
        chatArea.setVvalue(1.0);
    }

    public void setMe(final Player me) {
        this.me = me;
    }

    private void updatePlayerList() {
        // GUI Updates must be run later
        // https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

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

        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

    public void start(final ActionEvent event) {
        // server.startGame();
        var root = Main.FXML.load(GameMultiplayerController.class, "client", "scenes", "GameMultiplayer.fxml");
        root.getKey().setMe(me);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }
}
