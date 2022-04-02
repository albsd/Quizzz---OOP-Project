
package client;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import com.google.inject.Injector;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
        var mainCtrl = INJECTOR.getInstance(FXMLController.class);
        mainCtrl.initialize(stage, FXML);
    }

}
