package client.scenes;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpController {

    public void splash(final ActionEvent e) {
        var root = Main.FXML.load(
                SplashController.class, "client", "scenes", "Splash.fxml");

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root.getValue());
        stage.setScene(scene);
        stage.show();
    }
}
