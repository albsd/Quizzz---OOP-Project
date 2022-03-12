package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.google.inject.Inject;

import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;

import client.Main;
import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class IPPromptController implements Initializable {

    @Autowired
    private final ServerUtils server;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private Text warning;

    @Inject
    public IPPromptController(final ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        TextFormatter<Integer> numberFormatter = new TextFormatter<>(
                new IntegerStringConverter(),
                8080,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null);
        portField.setTextFormatter(numberFormatter);
    }

    @FXML
    public void connect(final ActionEvent event) {
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

        var root = Main.FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }

}
