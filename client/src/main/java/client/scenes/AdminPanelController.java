package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.Activity;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import javafx.util.converter.IntegerStringConverter;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    @FXML
    private TableView<Activity> table;
    @FXML
    private TableColumn<Activity, Long> id;
    @FXML
    private TableColumn<Activity, String> title;
    @FXML
    private TableColumn<Activity, String> source;
    @FXML
    private TableColumn<Activity, Long> consumption;
    @FXML
    private TableColumn<Activity, String> image;
    @FXML
    private TextField titleInput, powerInput, sourceInput, imageInput;
    @FXML
    private Label infoText;



    private final ServerUtils server;
    private final FXMLController fxml;
    private ObservableList<Activity> activities;
    private final Font font;

    @Inject
    public AdminPanelController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
    }

    @FXML
    public void loadTable() {
        List<Activity> listActivities = server.getAllActivity();
        activities = FXCollections.observableList(listActivities);
        table.setItems(activities);
    }

    @FXML
    public void submit() {
        Activity newActivity = new Activity(titleInput.getText(), Long.parseLong(powerInput.getText()),
                sourceInput.getText(), imageInput.getText());
        Activity added = server.addActivity(newActivity);
        infoText.setVisible(true);
        infoText.setText(String.format("New activity is added with the id %d.", added.getId()));
        titleInput.setText("");
        powerInput.setText("");
        sourceInput.setText("");
        imageInput.setText("");
        loadTable();
    }

    @FXML
    public void changeToAdd() {
        infoText.setVisible(false);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        id.setCellValueFactory(l -> new SimpleLongProperty(l.getValue().getId()).asObject());
        title.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getTitle()));
        source.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getSource()));
        consumption.setCellValueFactory(l -> new SimpleLongProperty(l.getValue().getEnergyConsumption()).asObject());
        image.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getPath()));
        powerInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("-?([1-9][0-9]*)?")) {
                        return change;
                    }
                    return null;
                }));
        powerInput.setText("");
        loadTable();
    }
}
