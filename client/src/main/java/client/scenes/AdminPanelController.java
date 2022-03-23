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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

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
        System.out.println(activities.get(0));
        table.setItems(activities);
    }

    @FXML
    public void submit() {
        Activity newActivity = new Activity(titleInput.getText(), Integer.parseInt(powerInput.getText()),
                sourceInput.getText(), imageInput.getText());
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        id.setCellValueFactory(l -> new SimpleLongProperty(l.getValue().getId()).asObject());
        title.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getTitle()));
        source.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getSource()));
        consumption.setCellValueFactory(l -> new SimpleLongProperty(l.getValue().getEnergyConsumption()).asObject());
        image.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getPath()));
        System.out.println("initalizedddd");
    }
}
