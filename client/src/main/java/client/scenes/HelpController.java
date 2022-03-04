package client.scenes;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {

    @FXML
    private Stage stage;

    private Scene scene;

    public void splash(final ActionEvent e) throws IOException {
        var root = Main.FXML.load(SplashController.class,
                "client", "scenes", "Splash.fxml");

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }
}
