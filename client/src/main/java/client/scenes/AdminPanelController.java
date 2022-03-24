package client.scenes;

import client.FXMLController;
import client.utils.ServerUtils;
import commons.Activity;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

//TODO: IMPROVE THE SCENE VISUALLY (FONTS - SIZES - PLACEMENT)
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
    private Label modeText, infoText;

    private final ServerUtils server;
    private final FXMLController fxml;
    private ObservableList<Activity> activities;
    private final Font font;
    private boolean isEditing;
    private Activity activityEdit;

    @Inject
    public AdminPanelController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
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
        isEditing = false;
        loadTable();
    }

    @FXML
    public void splash(final ActionEvent e) {
        fxml.showSplash();
    }

    @FXML
    public void loadTable() {
        List<Activity> listActivities = server.getAllActivity();
        activities = FXCollections.observableList(listActivities);
        table.setItems(activities);
        table.refresh();
        isEditing = false;
    }

    //TODO: CHECK FIELDS ARE NOT EMPTY AND THEY HAVE THE CORRECT FORMAT
    @FXML
    public void submit() {
        if (isEditing) {
            if (activityEdit == null) {
                infoText.setText("You have to select an activity to edit.");
            } else {
                activityEdit.setTitle(titleInput.getText());
                activityEdit.setEnergyConsumption(Long.parseLong(powerInput.getText()));
                activityEdit.setSource(sourceInput.getText());
                activityEdit.setPath(imageInput.getText());
                server.addActivity(activityEdit);
                infoText.setText(String.format("Activity with the id %d is edited.", activityEdit.getId()));
            }
        } else {
            Activity newActivity = new Activity(titleInput.getText(), Long.parseLong(powerInput.getText()),
                    sourceInput.getText(), imageInput.getText());
            Activity added = server.addActivity(newActivity);
            infoText.setText(String.format("New activity is added with the id %d.", added.getId()));
            titleInput.setText("");
            powerInput.setText("");
            sourceInput.setText("");
            imageInput.setText("");
        }
        loadTable();
    }

    public void add() {
        modeText.setText("ADD MODE");
        isEditing = false;
        activityEdit = null;
        infoText.setText("");
    }

    public void edit() {
        modeText.setText("EDIT MODE");
        isEditing = true;
        if (activityEdit != null) {
            infoText.setText(String.format("Activity with the id %d is selected to edit.", activityEdit.getId()));
        }
    }

    public void select() {
        activityEdit = getSelected();
        titleInput.setText(activityEdit.getTitle());
        powerInput.setText(Long.toString(activityEdit.getEnergyConsumption()));
        sourceInput.setText(activityEdit.getSource());
        imageInput.setText(activityEdit.getPath());
        if (isEditing) {
            infoText.setText(String.format("Activity with the id %d is selected to edit.", activityEdit.getId()));
        } else {
            infoText.setText("");
        }
    }

    public void delete() {
        Activity toBeDeleted = getSelected();
        infoText.setText(String.format("Activity with the id %d is deleted.", toBeDeleted.getId()));
        server.deleteActivity(toBeDeleted.getId());
        loadTable();
    }

    public Activity getSelected() {
        return table.getSelectionModel().getSelectedItem();
    }
}
