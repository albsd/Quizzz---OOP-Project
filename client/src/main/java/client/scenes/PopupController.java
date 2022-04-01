package client.scenes;

import client.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import javax.inject.Inject;

public class PopupController {

    @FXML
    private Pane menu;

    @FXML
    private Label text;

    private final FXMLController fxml;

    private Runnable onConfirm;

    @Inject
    public PopupController(final FXMLController fxml) {
        this.fxml = fxml;
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

    /**
     * First unsubscribe from messages, only then execute the callback.
     */
    @FXML
    public void returnToMenu() {
        fxml.showSplash();
        onConfirm.run();
    }
}
