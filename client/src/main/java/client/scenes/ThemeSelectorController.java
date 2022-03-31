package client.scenes;

import client.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class ThemeSelectorController {

    private final FXMLController fxml;

    @Inject
    public ThemeSelectorController(final FXMLController fxml) {
        this.fxml = fxml;
    }

    @FXML
    public void splash(final ActionEvent e) {
        fxml.showSplash();
    }

    @FXML
    public void setLightheme() {
        fxml.setTheme("css/light.css");
    }

    @FXML
    public void setDarkTheme() {
        fxml.setTheme("css/dark.css");
    }
}
