package client.scenes;

import javax.inject.Inject;

import client.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HelpController {

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
