
package client;

import client.scenes.SplashController;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serial;

import static com.google.inject.Guice.createInjector;



public class Main extends Application {

        private static final Injector INJECTOR = createInjector(new MyModule());
        private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(final String[] args) {
        launch();
    }

    @Override
<<<<<<< HEAD
    public void start(final Stage stage) throws IOException {
=======
    public void start(Stage stage) throws IOException, InterruptedException {
>>>>>>> adcf1b52978bcc02729e674b8d25d347c631fb94
//        Parent root = FXMLLoader.load(Main.class.getResource("Splash.fxml"));
        var root  = FXML.load(SplashController.class,
                "client", "scenes", "Splash.fxml");
        Scene scene = new Scene(root.getValue());
        Image logo = new Image(Main.class
                .getResourceAsStream("/images/icon.png"));
        stage.getIcons().add(logo);
        stage.setTitle("Energy Quizzz");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        ServerUtils server = new ServerUtils();
        System.out.println(server.getLeaderboard("9bec99d8-49ae-445b-b77a-103eee9be154"));
    }

}
