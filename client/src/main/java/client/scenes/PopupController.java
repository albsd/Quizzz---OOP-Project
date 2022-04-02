package client.scenes;

import client.FXMLController;
import client.sounds.Sound;
import client.sounds.SoundName;
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
        Sound pop = new Sound(SoundName.pop);
        pop.play(false, false);
        menu.setVisible(false);
    }

    /**
     * First unsubscribe from messages, only then execute the callback.
     */
    @FXML
    public void returnToMenu() {
        Sound pop = new Sound(SoundName.pop);
        pop.play(false, false);
        fxml.showSplash();
        onConfirm.run();
    }
}
