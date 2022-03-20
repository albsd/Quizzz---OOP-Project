package client.scenes;

import client.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class PopupController implements Initializable {

    @FXML
    private Button cancelButton, confirmButton;

    @FXML
    private Pane menu;

    @FXML
    private Label text;

    private final FXMLController fxml;

    private final Font font;

    private Runnable onConfirm;

    @Inject
    public PopupController(final FXMLController fxml) {
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        text.setFont(font);
        cancelButton.setFont(font);
        confirmButton.setFont(font);
    }

    public void open(final String what, final Runnable onConfirm) {
        menu.setVisible(true);
        this.text.setText("Do you really want to leave the " + what + "?");
        this.onConfirm = onConfirm;
    }

    @FXML
    public void close(final ActionEvent e) {
        menu.setVisible(false);
    }

    @FXML
    public void returnToMenu() {
        onConfirm.run();
        fxml.showSplash();
    }
}
