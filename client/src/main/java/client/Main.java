
package client;

import client.scenes.SplashController;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        // Parent root = FXMLLoader.load(Main.class.getResource("Splash.fxml"));
        var root = FXML.load(SplashController.class,
                "client", "scenes", "Splash.fxml");
        Scene scene = new Scene(root.getValue());
        Image logo = new Image(Main.class
                .getResourceAsStream("/images/icon.png"));
        stage.getIcons().add(logo);
        stage.setTitle("Energy Quizzz");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // ServerUtils server = new ServerUtils();
        // server.joinGame("lolo");
        // System.out.println(server.createGame());
    }

}
