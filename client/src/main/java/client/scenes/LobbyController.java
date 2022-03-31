package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;
import commons.Game;
import commons.GameUpdate;
import commons.LobbyMessage;
import commons.Player;
import commons.PlayerUpdate;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javafx.scene.text.Font;
import javafx.util.Duration;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public class LobbyController implements Initializable, WebSocketSubscription {

    @FXML
    private ScrollPane chatArea;

    @FXML
    private Label chatText, playersLeft, playersRight, playerCount, title, chatTitle;

    @FXML
    private Button startButton;

    @FXML
    private TextField chatInput;

    @FXML
    private Parent popup;

    @FXML
    private PopupController popupController;

    private final Font font1, font2, font3;

    private final ServerUtils server;
    
    private final FXMLController fxml;
    
    private Player me;
    
    private List<String> players;

    private Game lobby;

    @Inject
    public LobbyController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.players = new ArrayList<>();
        this.font1 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
        this.font2 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 30);
        this.font3 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 72);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // We DON'T use the shorthand .toList() here, because that returns an immutable
        // list and causes player updates to get ignored silently
        this.lobby = server.getLobby();
        this.players = lobby.getPlayers().stream()
                .map(Player::getNick)
                .collect(Collectors.toList());

        updatePlayerList();

        chatText.setFont(font1);
        playersLeft.setFont(font1);
        playersRight.setFont(font1);
        playerCount.setFont(font2);
        title.setFont(font3);
        chatTitle.setFont(font2);
        startButton.setFont(font1);
        chatInput.setFont(font1);
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[3];
        subscriptions[0] = server.registerForMessages("/topic/update/player", PlayerUpdate.class, update -> {
            if (update.getContent() == PlayerUpdate.Type.join) {
                players.add(update.getNick());
            } else {
                players.remove(update.getNick());
            }
            updatePlayerList();
        });

        subscriptions[1] = server.registerForMessages("/topic/lobby/chat", LobbyMessage.class, message -> {
            Platform.runLater(() -> {
                //prevent repeated clicking of start button
                if (message.toString().startsWith("Server - Game is about to start.")) {
                    startButton.setDisable(true);
                }
                String chatBox = chatText.getText() + message.toString();
                chatText.setText(chatBox);
                chatArea.layout();
                chatArea.setVvalue(1.0);
            });
        });

        subscriptions[2] = server.registerForMessages("/topic/lobby/start", GameUpdate.class, update -> {
            KeyFrame kf = new KeyFrame(Duration.seconds(3), e -> {
                server.cancelHeartbeat();
                Game game = server.getGameById(lobby.getId());
                fxml.showMultiPlayer(me, game);
            });
            Timeline timeline = new Timeline(kf);
            Platform.runLater(timeline::play);
        });

        return subscriptions;
    }

    @FXML
    public void onEnter(final ActionEvent e) {
        String content = chatInput.getText().replaceAll("[\"\'><&]", ""); // escape XML characters
        chatInput.setText("");

        final ZonedDateTime time = ZonedDateTime.now();
        final LobbyMessage message = new LobbyMessage(me.getNick(), time.toString(), content);
        
        server.send("/app/lobby/chat", message);
    }

    public void setMeAndTask(final Player me) {
        this.me = me;
        server.startHeartbeat(new TimerTask() {
            @Override
            public void run() {
                server.updateLobbyPlayer(me.getNick());
            }
        });
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
    public void openPopup(final ActionEvent event) {
        popupController.open("lobby", () -> {
            server.leaveLobby(me.getNick());
            server.cancelHeartbeat();
            fxml.showSplash();
        });
    }

    @FXML
    public void start(final ActionEvent event) {
        //don't start game immediately cause invoker starts game faster
        //than other players in lobby
        server.startMultiPlayer();
    }
}
