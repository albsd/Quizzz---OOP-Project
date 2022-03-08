package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.function.Consumer;

public class LobbyController {

    @FXML
    private ScrollPane chatArea;

    @FXML
    private Label chatText;

    @FXML
    private TextField chatInput;

    private Stage stage;

    private Scene scene;

    private String nickname;

    private final ServerUtils server;

    @Inject
    public LobbyController(final ServerUtils server) {
        this.server = server;
        server.registerForMessages("/topic/lobby/chat",
                Message.class, messageConsumer);
    }

    @FXML
    public void onEnter(final ActionEvent e) {
        String content = chatInput.getText();
        chatInput.setText("");
        final int demoTime = 10;
        //escapes special characters in input
        server.send("/app/lobby/chat",
                new Message(getNickname(), demoTime,
                        HtmlUtils.htmlEscape(content)));
    }

    private Consumer<Message> messageConsumer = m -> {
        System.out.println("Message received");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String nick = m.getNick();
                int time = m.getTime();
                String content = m.getMessageContent();
                //change. Scroll pane is not place to put messages
                String chatLogs = chatText.getText()
                        + "\n" + nick + " (" + time + ") - " + content;
                chatText.setText(chatLogs);
            }
        });
    };

    public void returnMenu(final ActionEvent e) throws IOException {
        var root = Main.FXML.load(
                SplashController.class, "client", "scenes", "Splash.fxml");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }


    public void setNickname(final String nickname) {
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
