package client.scenes;

import javax.inject.Inject;

import client.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    private Label title, powerupText, powerup1Text, powerup2Text, powerup3Text,
            chatText, leaveText, howToText1, howToText2, howToText3, singleText, multiText;
    private final Font font1, font2, font3;

    private final FXMLController fxml;

    @Inject
    public HelpController(final FXMLController fxml) {
        this.fxml = fxml;
        this.font1 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
        this.font2 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 29);
        this.font3 = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 72);
    }

    @FXML
    public void splash(final ActionEvent e) {
        fxml.showSplash();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        powerup1Text.setFont(font1);
        powerup2Text.setFont(font1);
        powerup3Text.setFont(font1);
        leaveText.setFont(font1);
        howToText1.setFont(font1);
        howToText2.setFont(font1);
        howToText3.setFont(font1);

        powerupText.setFont(font2);
        chatText.setFont(font2);
        singleText.setFont(font2);
        multiText.setFont(font2);

        title.setFont(font3);
    }
}
