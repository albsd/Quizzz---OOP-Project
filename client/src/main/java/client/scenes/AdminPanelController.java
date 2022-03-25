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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    private TextField titleInput, powerInput, sourceInput;
    @FXML
    private Label modeText, infoText;
    @FXML
    private ImageView imageShow;

    private final ServerUtils server;
    private final FXMLController fxml;
    private ObservableList<Activity> activities;
    private final Font font;
    private boolean isEditing;
    private Activity activityEdit;
    private commons.Image image;
    private final FileChooser.ExtensionFilter extensionFilter;

    @Inject
    public AdminPanelController(final ServerUtils server, final FXMLController fxml) {
        this.server = server;
        this.fxml = fxml;
        this.font = Font.loadFont(getClass().getResourceAsStream("/fonts/Righteous-Regular.ttf"), 24);
        this.extensionFilter = new FileChooser.ExtensionFilter("Images", "*.jpeg", "*.jpg", "*.png");
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        id.setCellValueFactory(l -> new SimpleLongProperty(l.getValue().getId()).asObject());
        title.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getTitle()));
        source.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getSource()));
        consumption.setCellValueFactory(l -> new SimpleLongProperty(l.getValue().getEnergyConsumption()).asObject());
        powerInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("([1-9][0-9]*)?")) {
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
    }

    @FXML
    public void submit() {
        if (isEditing) {
            if (activityEdit == null) {
                infoText.setText("You have to select an activity to edit.");
            } else {
                if (!checkInput()) return;
                activityEdit.setTitle(titleInput.getText());
                activityEdit.setEnergyConsumption(Long.parseLong(powerInput.getText()));
                activityEdit.setSource(sourceInput.getText());
                activityEdit.setPath(image.getName());
                server.addActivity(activityEdit);
                server.sendImage(image);
                infoText.setText(String.format("Activity with the id %d is edited.", activityEdit.getId()));
            }
        } else {
            if (image == null) {
                infoText.setText("You need to select an image before adding an activity.");
            } else {
                if (!checkInput()) return;
                Activity newActivity = new Activity(titleInput.getText(), Long.parseLong(powerInput.getText()),
                        sourceInput.getText(), image.getName());
                Activity added = server.addActivity(newActivity);
                server.sendImage(image);
                infoText.setText(String.format("New activity is added with the id %d.", added.getId()));
                titleInput.setText("");
                powerInput.setText("");
                sourceInput.setText("");
                imageShow.setImage(null);
                image = null;
            }
        }
        loadTable();
    }

    private boolean checkInput() {
        if (titleInput.getText().equals("")) {
            infoText.setText("Title cannot be empty.");
            return false;
        } else if (powerInput.getText().equals("")) {
            infoText.setText("Power consumption cannot be empty.");
            return false;
        } else if (sourceInput.getText().equals("")) {
            infoText.setText("Source cannot be empty.");
            return false;
        } else {
            return true;
        }
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
        image = server.getImage(activityEdit.getPath());
        imageShow.setImage(new Image(new ByteArrayInputStream(image.getData())));

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
        titleInput.setText("");
        powerInput.setText("");
        sourceInput.setText("");
        imageShow.setImage(null);
        image = null;
        loadTable();
    }

    public void chooseImage() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            image = new commons.Image(generateImageByteArray(file.getPath()),
                    file.getName().replaceAll("\\s", "_"));
            imageShow.setImage(new Image(new ByteArrayInputStream(image.getData())));
        }
    }

    private byte[] generateImageByteArray(final String imagePath) {
        // TODO: add the path to the default image
        if (imagePath == null) return new byte[0];
        File file = new File(imagePath);
        String extension = imagePath.substring(imagePath.lastIndexOf('.') + 1);
        try {
            BufferedImage bImage = ImageIO.read(file);
            if (bImage == null) {
                return new byte[0];
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, extension, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
            return new byte[0];
        }
    }

    public Activity getSelected() {
        return table.getSelectionModel().getSelectedItem();
    }
}
