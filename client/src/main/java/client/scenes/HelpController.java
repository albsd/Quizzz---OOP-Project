package client.scenes;

import javax.inject.Inject;

import client.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelpController {

    @FXML
    private Label title, powerupText, powerup1Text, powerup2Text, powerup3Text,
            chatText, leaveText, howToText1, howToText2, howToText3, singleText, multiText;

    private final FXMLController fxml;

    @Inject
    public HelpController(final FXMLController fxml) {
        this.fxml = fxml;
    }

    @FXML
    public void splash(final ActionEvent e) {
        fxml.showSplash();
    }
}
