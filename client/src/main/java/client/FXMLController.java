package client;

import client.scenes.GameController;
import client.scenes.HelpController;
import client.scenes.IPPromptController;
import client.scenes.LeaderboardController;
import client.scenes.LobbyController;
import client.scenes.SplashController;
import javafx.animation.PauseTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class FXMLController {

    private Stage primaryStage;

    private MyFXML myFXML;

    /**
     * Store the primaryStage of the application and MyFXML reference for
     * loading the fxml files with the associated controller.
     * 
     * @param primaryStage PrimaryStage of the application
     * @param myFXML       Custom FXML loader
     */
    public void initialize(final Stage primaryStage, final MyFXML myFXML) {
        this.primaryStage = primaryStage;
        this.myFXML = myFXML;

        Image logo = new Image(Main.class.getResourceAsStream("/images/icon.png"));
        primaryStage.getIcons().add(logo);
        primaryStage.setTitle("Energy Quizzz");
        primaryStage.setResizable(false);

        showIPPrompt();
    }

    /**
     * Displays the scene of the primaryStage.
     * Assume that each scene's controller class is named <scene_name>Controller.
     * Therefore we can parse the name of the class to location of the fxml's file.
     * 
     * @param <T>  Generic type for the controller's class
     * @param type Type of the controller for which to set the scene
     * @return Pair<T, Parent> for the controller's type
     */
    private <T> Pair<T, Parent> displayScene(final Class<T> type) {
        String file = type.getSimpleName().replace("Controller", ".fxml");
        var root = myFXML.load(type, "client", "scenes", file);
        Scene scene = new Scene(root.getValue());
        primaryStage.setScene(scene);
        primaryStage.show();
        return root;
    }


    public Pair<IPPromptController, Parent> showIPPrompt() {
        return displayScene(IPPromptController.class);
    }

    public Pair<SplashController, Parent> showSplash() {
        return displayScene(SplashController.class);
    }

    public Pair<LeaderboardController, Parent> showLeaderboard() {
        return displayScene(LeaderboardController.class);
    }

    public Pair<HelpController, Parent> showHelp() {
        return displayScene(HelpController.class);
    }

    public Pair<LobbyController, Parent> showLobby() {
        return displayScene(LobbyController.class);
    }

    public Pair<GameController, Parent> showGame() {
        return displayScene(GameController.class);
    }

    public Pair<LeaderboardController, Parent> displayLeaderboardMomentarily(
            final Class<LeaderboardController> type) {
        String file = type.getSimpleName().replace("Controller", ".fxml");
        var root = myFXML.load(type, "client", "scenes", file);
        Stage stage1 = new Stage();
        Scene scene = new Scene(root.getValue());
        stage1.setScene(scene);
        stage1.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> stage1.close());
        return root;
    }
}
