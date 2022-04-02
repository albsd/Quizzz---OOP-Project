package client.scenes;

import client.FXMLController;
import client.sounds.Sound;
import client.sounds.SoundName;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;

import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public class LobbyController implements Initializable, WebSocketSubscription {

    @FXML
    private ScrollPane chatArea;

    @FXML
    private Label chatText, playersLeft, playersRight, playerCount;

    @FXML
    private Button startButton;

    @FXML
    private SVGPath soundIcon;

    @FXML
    private TextField chatInput;

    @FXML
    private PopupController popupController;

    private final ServerUtils server;
    
    private final FXMLController fxml;
    
    private Player me;
    
    private List<String> players;

    private Game lobby;

    private boolean muted;

    private Sound lobbyMusic;

    @Inject
    public LobbyController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.players = new ArrayList<>();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // We DON'T use the shorthand .toList() here, because that returns an immutable
        // list and causes player updates to get ignored silently
        lobbyMusic = new Sound(SoundName.lobby_music);
        lobbyMusic.play(muted, true);
        this.lobby = server.getLobby();
        this.players = lobby.getPlayers().stream()
                .map(Player::getNick)
                .collect(Collectors.toList());
        updatePlayerList();
        muted = false;
    }

    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[3];
        subscriptions[0] = server.registerForMessages("/topic/update/player", PlayerUpdate.class, update -> {
            Sound messageSound = new Sound(SoundName.chat_message);
            messageSound.play(muted, false);

            if (update.getContent() == PlayerUpdate.Type.join) {
                players.add(update.getNick());
            } else {
                players.remove(update.getNick());
            }
            updatePlayerList();
        });

        subscriptions[1] = server.registerForMessages("/topic/lobby/chat", LobbyMessage.class, message -> {
            Sound messageSound = new Sound(SoundName.chat_message);
            messageSound.play(muted, false);

            Platform.runLater(() -> {
                //prevent repeated clicking of start button
                if (message.toString().startsWith("Server - Game is about to start.")) {
                    startButton.setDisable(true);
                    lobbyMusic.stop();
                }
                String chatBox = chatText.getText() + message.toString();
                chatText.setText(chatBox);
                chatArea.layout();
                chatArea.setVvalue(1.0);
            });
        });

        subscriptions[2] = server.registerForMessages("/topic/lobby/start", GameUpdate.class, update -> {
            Sound lobbyStartSound = new Sound(SoundName.lobby_start);
            lobbyStartSound.play(muted, false);

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
        Sound messageSound = new Sound(SoundName.chat_message);
        messageSound.play(muted, false);
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
            lobbyMusic.stop();
            server.leaveLobby(me.getNick());
            server.cancelHeartbeat();
            fxml.showSplash();
        });
    }

    @FXML
    public void start(final ActionEvent event) {
        server.startMultiPlayer();
    }

    @FXML
    private void updateSoundButton(final ActionEvent e) {
        if (muted) {
            muted = false;
            lobbyMusic.unmuteVolume();
            soundIcon.setContent(loadSVGPath("/images/svgs/sound.svg"));
        } else {
            muted = true;
            lobbyMusic.muteVolume();
            soundIcon.setContent(loadSVGPath("/images/svgs/mute.svg"));
        }
    }

    public String loadSVGPath(final String filePath) {
        try {
            Scanner svgScanner = new Scanner(getClass().getResource(filePath).openStream(), StandardCharsets.UTF_8);
            svgScanner.skip(".*<path d=\"");
            svgScanner.useDelimiter("\"");
            String svgString = svgScanner.next();
            svgScanner.close();
            return svgString;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
