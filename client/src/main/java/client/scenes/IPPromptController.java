package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import client.sounds.Sound;
import client.sounds.SoundName;
import com.google.inject.Inject;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import client.FXMLController;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;

public class IPPromptController implements Initializable {
    
    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private Text warning;

    @FXML
    private Button connectButton;
    
    private final ServerUtils server;

    private final FXMLController fxml;

    @Inject
    public IPPromptController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        TextFormatter<Integer> numberFormatter = new TextFormatter<>(
                new IntegerStringConverter(),
                8080,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null);
        portField.setTextFormatter(numberFormatter);

        hostField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                portField.requestFocus();
            }
        });

        portField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                connectButton.requestFocus();
            }
        });

        connectButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                connect();
            }
        });
    }

    @FXML
    public void connect() {
        new Sound(SoundName.pop).play(false, false);
        String host = hostField.getText().replaceAll("[\"\'><&]", ""); // escape XML characters
        String port = portField.getText();

        String error = server.isRunning(host, port);
        if (error != null) {
            warning.setFill(new Color(0.8, 0, 0, 1));
            warning.setText(error);
            return;
        }

        warning.setFill(new Color(0, 0.6, 0, 1));
        warning.setText("Connected to http://" + host + ":" + port);
        fxml.showSplash();
    }
}
