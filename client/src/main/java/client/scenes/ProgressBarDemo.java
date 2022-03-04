package client.scenes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProgressBarDemo extends Application {
    public static void main(final String[] args) {
        launch();
    }
    @Override
    public void start(final Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(ProgressBarDemo.class.getResource(
                        "progress-bar.fxml"));
        final int width = 600;
        final int height = 145;
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
