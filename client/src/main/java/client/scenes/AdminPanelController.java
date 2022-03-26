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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
        this.extensionFilter = new FileChooser.ExtensionFilter("Images", "*.jpeg", "*.jpg",
                "*.png", "*.gif");
    }

    /**
     * Sets up the table and fields to view activities and loads activities.
     * @param location
     * @param resources
     */
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
        loadTable(false);

        //Activity selection with double-clicking
        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                select();
            }
        });
    }

    /**
     * Loads the splash screen.
     * @param e
     */
    @FXML
    public void splash(final ActionEvent e) {
        fxml.showSplash();
    }

    /**
     * Gets activities from the server/database.
     * and refreshes the table.
     * @param scroll Scrolls to the bottom if true
     */
    public void loadTable(final boolean scroll) {
        List<Activity> listActivities = server.getAllActivities();
        activities = FXCollections.observableList(listActivities);
        table.setItems(activities);
        table.refresh();

        if (scroll) {
            table.scrollTo(table.getItems().size() - 1);
        }
    }

    @FXML
    public void refresh() {
        loadTable(false);
    }

    /**
     * Submits the filled inputs to the server, either adding or editing an activity.
     * Checks whether fields and image are entered properly
     * Shows the user appropriate message in the info text
     */
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
                loadTable(false);
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
                loadTable(true);
            }
        }
    }

    /**
     * Checks if the title, power consumption and source fields are entered properly and warns user.
     * @return return false if any of the mentioned fields are empty and true otherwise
     */
    private boolean checkInput() {
        if (titleInput.getText().equals("")) {
            infoText.setText("Title cannot be empty.");
            return false;
        }
        if (powerInput.getText().equals("")) {
            infoText.setText("Power consumption cannot be empty.");
            return false;
        }
        if (sourceInput.getText().equals("")) {
            infoText.setText("Source cannot be empty.");
            return false;
        }
        return true;
    }

    /**
     * Changes current mode to ADD MODE.
     */
    public void add() {
        modeText.setText("ADD MODE");
        isEditing = false;
        activityEdit = null;
        infoText.setText("");
    }

    /**
     * Changes current mode to EDIT MODE.
     */
    public void edit() {
        modeText.setText("EDIT MODE");
        isEditing = true;
        if (activityEdit != null) {
            infoText.setText(String.format("Activity with the id %d is selected to edit.", activityEdit.getId()));
        }
    }

    /**
     * Selects an activity from the table.
     * Fills required fields and uploads the image of the activity.
     */
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

    /**
     * Deletes the selected activity from the database and the image of the activity.
     */
    public void delete() {
        Activity toBeDeleted = getSelected();
        infoText.setText(String.format("Activity with the id %d is deleted.", toBeDeleted.getId()));
        server.deleteActivity(toBeDeleted.getId());
        titleInput.setText("");
        powerInput.setText("");
        sourceInput.setText("");
        imageShow.setImage(null);
        image = null;
        loadTable(false);
    }

    /**
     * Opens up the file chooser tab for image selection.
     * Shows up the image if an appropriate image is selected.
     */
    public void chooseImage() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && file.length() / 1024 <= 500) {

            image = new commons.Image(generateImageByteArray(file.getPath()),
                    file.getName().replaceAll("\\s", "_"));
            Image img = new Image(new ByteArrayInputStream(image.getData()));
            if (img.getHeight() < 200) {
                infoText.setText("Image height must be greater than 200px.");
                image = null;
            } else if (img.getWidth() < 200) {
                infoText.setText("Image width must be greater than 200px.");
                image = null;
            } else {
                imageShow.setImage(img);
            }
        } else {
            infoText.setText("File size must be smaller than 500KB.");
        }
    }

    /**
     * Reads the image file and turns it into byte array.
     * @param imagePath Path of the image to be read
     * @return Data of the image to be read as byte array
     */
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

    /**
     * @return Currently selected activity in the table
     */
    public Activity getSelected() {
        return table.getSelectionModel().getSelectedItem();
    }
}
