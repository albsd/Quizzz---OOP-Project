package client.scenes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SplashApplication extends Application {


    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(SplashApplication.class.getResource("Splash.fxml"));
            Scene scene = new Scene(root);
//            Image logo = new Image(SplashApplication.class.getResourceAsStream("icon.png"));
//            stage.getIcons().add(logo);
            stage.setTitle("Energy Quizzz");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
