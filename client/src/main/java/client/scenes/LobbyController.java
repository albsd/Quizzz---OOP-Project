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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LobbyController{

    @FXML
    private ScrollPane chatArea;

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
        server.registerForMessages("/topic/lobby/chat", Message.class, messageConsumer);

        chatInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                String content = chatInput.getText();
                chatInput.setText("");
                //escapes special characters in input
                server.send("/app/lobby/chat", new Message(getNickname(), 23, HtmlUtils.htmlEscape(content)));
            }
        });
    }

    private Consumer<Message> messageConsumer = m -> {
        System.out.println("Message received");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String nickname = m.getNick();
                int time = m.getTime();
                String content = m.getMessageContent();
                //change. Scroll pane is not place to put messages
                TextField text = new TextField();
                text.setText(nickname + " (" + time + ") - " + content);
                chatArea.setContent(text);
                chatArea.setPannable(true);
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


    public void sendNickname(final String nickname) {
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
