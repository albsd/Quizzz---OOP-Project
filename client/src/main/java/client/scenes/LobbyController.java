package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Message;
import commons.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.util.HtmlUtils;

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

    private boolean left = true;

    @FXML
    private Label playerCount;

    private Stage stage;

    private Scene scene;

    private String nickname;

    private final List<Player> players = new ArrayList<>();

    private final ServerUtils server;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        server.registerForMessages("/topic/join", Player.class, playerConsumer);
        server.registerForMessages("/topic/lobby/chat", Message.class, messageConsumer);

        chatInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                String chatMsg = chatInput.getText();
                chatInput.setText("");
                try {
                    sendMessage(chatMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        List<Player> lobbyPlayers = null;
        lobbyPlayers = server.getPlayers();
        if (lobbyPlayers != null) {
            for (Player p : lobbyPlayers) {
                playerConsumer.accept(p);
            }
        }
    }
    /**
     * private utilities related to websockets. Receives broadcast from server
     */
    private Consumer<Player> playerConsumer = p -> {
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

    private Consumer<Message> messageConsumer = m -> {
        System.out.println("Message received");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String content = m.getMessageContent();
            }
        });
    };


    public void returnMenu(final ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("Splash.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    //app/lobby/chat
    @MessageMapping("/lobby/chat")
    //whatever is in mapping should be beneath after topic
    @SendTo("/topic/lobby/chat")
    private Message sendMessage(final String msg) throws InterruptedException {
        Thread.sleep(1000);
        //escapes special characters in input
        Message message = new Message(HtmlUtils.htmlEscape(msg));
        message.setNick(getNickname());
        message.setTime(24);
        return message;
    }

    public void sendNickname(String nickname){
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void start(final ActionEvent event) {
        // TODO: display the multiplayer fxml
        // server.startGame();
    }
}
