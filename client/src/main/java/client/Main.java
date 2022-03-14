
package client;

import client.scenes.IPPromptController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    public static final MyFXML FXML = new MyFXML(createInjector(new MyModule()));

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        Image logo = new Image(Main.class.getResourceAsStream("/images/icon.png"));
        stage.getIcons().add(logo);
        stage.setTitle("Energy Quizzz");

        var root = Main.FXML.load(IPPromptController.class, "client", "scenes", "IPPrompt.fxml");
        Scene scene = new Scene(root.getValue());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}
