package client;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import client.scenes.IPPromptController;
import client.scenes.LobbyController;
import client.scenes.GameController;
import client.scenes.SplashController;
import client.scenes.LeaderboardController;
import client.scenes.HelpController;
import client.scenes.AdminPanelController;
import client.scenes.ThemeSelectorController;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import commons.Game;
import commons.Leaderboard;
import commons.Player;
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
    
    private List<Subscription> lobbySubscriptions;

    private List<Subscription> gameSubscriptions;

    private String themePath = "css/dark.css";

    private String nick;

    /**
     * Store the primaryStage of the application and MyFXML reference for
     * loading the fxml files with the associated controller.
     * 
     * @param primaryStage PrimaryStage of the application
     * @param myFXML       Custom FXML loader
     */
    public void initialize(final Stage primaryStage, final MyFXML myFXML) {
        this.myFXML = myFXML;
        this.primaryStage = primaryStage;
        this.lobbySubscriptions = new ArrayList<>();
        this.gameSubscriptions = new ArrayList<>();

        Image logo = new Image(Main.class.getResourceAsStream("/images/icon.png"));
        primaryStage.getIcons().add(logo);
        primaryStage.setTitle("Energy Quizzz");
        primaryStage.setResizable(false);

        displayScene(IPPromptController.class);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    /**
     * Displays the scene of the primaryStage.
     * Assume that each scene's controller class is named <scene_name>Controller.
     * Therefore we can parse the name of the class to location of the fxml's file.
     * 
     * @param <T>  Generic type for the controller's class
     * @param type Type of the controller for which to set the scene
     * @return     Pair<T, Parent> for the controller's type
     */
    private <T> Pair<T, Parent> displayScene(final Class<T> type) {
        String file = type.getSimpleName().replace("Controller", ".fxml");
        var root = myFXML.load(type, "client", "scenes", file);
        Scene scene = new Scene(root.getValue());
        scene.getStylesheets().addAll("css/base.css", themePath);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        return root;
    }

    /**
     * Re-initialize subscripition for the screen that is being set.
     * Unsubscribe from all the other screen's subscriptions.
     * 
     * @param <T>           Generic type for the controller's class
     * @param type          Type of the controller for which to set the scene
     * @param subscriptions Array of subscriptions to re-initialize
     */
    private <T> void subscribe(final Class<T> type, final Subscription... subscriptions) {
        this.lobbySubscriptions.forEach(Subscription::unsubscribe);
        this.gameSubscriptions.forEach(Subscription::unsubscribe);
        if (type == LobbyController.class) {
            this.lobbySubscriptions = Arrays.asList(subscriptions);
        } else if (type == GameController.class) {
            this.gameSubscriptions = Arrays.asList(subscriptions);
        }
    }

    public void setTheme(final String themePath) {
        this.themePath = themePath;
        var style = primaryStage.getScene().getStylesheets();
        style.clear();
        style.addAll("css/base.css", themePath);
    }

    public Pair<SplashController, Parent> showSplash() {
        subscribe(SplashController.class);
        return displayScene(SplashController.class);
    }

    public Pair<LeaderboardController, Parent> showLeaderboard(final Leaderboard leaderboard) {
        subscribe(LeaderboardController.class);
        var root = displayScene(LeaderboardController.class);
        LeaderboardController leaderboardController = root.getKey();
        leaderboardController.show();
        leaderboardController.displayLeaderboard(leaderboard, new Player(nick));
        return root;
    }

    public Pair<HelpController, Parent> showHelp() {
        subscribe(HelpController.class);
        return displayScene(HelpController.class);
    }

    public Pair<AdminPanelController, Parent> showAdminPanel() {
        subscribe(AdminPanelController.class);
        return displayScene(AdminPanelController.class);
    }

    public Pair<LobbyController, Parent> showLobby(final Player me) {
        var root = displayScene(LobbyController.class);
        var ctrl = root.getKey(); 
        ctrl.setMeAndTask(me);
        subscribe(LobbyController.class, ctrl.registerForMessages());
        return root;
    }

    public Pair<GameController, Parent> showMultiPlayer(final Player me, final Game game) {
        var root = displayScene(GameController.class);
        var ctrl = root.getKey();
        ctrl.setGame(me, game);
        subscribe(GameController.class, ctrl.registerForMessages());
        return root;
    }

    public Pair<GameController, Parent> showSinglePlayer(final Game game) {
        var root = displayScene(GameController.class);
        var ctrl = root.getKey();
        ctrl.setSinglePlayer(game);

        subscribe(GameController.class);
        return root;
    }

    public Pair<ThemeSelectorController, Parent> showThemeSelector() {
        subscribe(ThemeSelectorController.class);
        return displayScene(ThemeSelectorController.class);
    }

    public Pair<LeaderboardController, Parent> displayLeaderboardMomentarily() {
        var root = myFXML.load(LeaderboardController.class, "client", "scenes", "Leaderboard.fxml");
        Stage stage1 = new Stage();
        Scene scene = new Scene(root.getValue());
        stage1.setScene(scene);
        stage1.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> stage1.close());
        return root;
    }

    public void saveNick(final String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return this.nick;
    }

    public String loadSVGPath(final String filePath) {
        try {
            Scanner svgScanner = new Scanner(getClass().getResource(filePath).openStream(), StandardCharsets.UTF_8);
            svgScanner.skip(".*<path d=\"");
            svgScanner.useDelimiter("\"");
            String svgString = svgScanner.next();
            svgScanner.close();
            return svgString;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
