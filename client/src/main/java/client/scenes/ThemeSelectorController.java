package client.scenes;

import client.FXMLController;
import client.sounds.Sound;
import client.sounds.SoundName;
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
        new Sound(SoundName.click).play(false, false);
        fxml.showSplash();
    }

    @FXML
    public void setLightheme() {
        new Sound(SoundName.pop).play(false, false);
        fxml.setTheme("css/light.css");
    }

    @FXML
    public void setDarkTheme() {
        new Sound(SoundName.pop).play(false, false);
        fxml.setTheme("css/dark.css");
    }

    @FXML
    public void setAquaTheme() {
        new Sound(SoundName.pop).play(false, false);
        fxml.setTheme("css/aqua.css");
    }

    @FXML
    public void setPineTheme() {
        new Sound(SoundName.pop).play(false, false);
        fxml.setTheme("css/pine.css");
    }
}
