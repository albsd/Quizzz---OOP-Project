package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {

    @FXML
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void splash(ActionEvent e) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Splash.fxml"));
        root = loader.load();

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
