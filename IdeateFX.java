import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.NumberStringConverter;


/**
 * @author Prakhar Mittal
 * @version 2.0
 * This application allows users to submit startup ideas and manage them.
 */
public class IdeateFX extends Application {

    // Observable List to store ideas
    private static ObservableList<StartUpIdea> ideas;

    // Form labels and fields
    private static Label titleLabel, problemLabel, targetCustomerLabel, customerNeedLabel,
        feasibilityLabel, knownPeopleWithProblemLabel, targetMarketSizeLabel, competitorsLabel;
    private static TextArea problemField, targetCustomerField, competitorsField;
    private static TextField knownPeopleWithProblemField, targetMarketSizeField;
    private static Slider customerNeedField, feasibilityField;

    // Files required
    private static File file = new File("ideas.txt");
    private static Media success = new Media(new File("success.mp3").toURI().toString());
    private static Media error = new Media(new File("error.mp3").toURI().toString());

    /**
     * Starting point for application, loads elements, and sets scene.
     * @param stage application stage
     */
    public void start(Stage stage) {

        // Load existing ideas from file into list
        ideas = loadFromFile();

        // Load UI elements
        VBox header = getHeader();
        VBox left = getLeft();
        VBox right = getRight();

        // Grid Pane for columns
        GridPane columns = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        columns.getColumnConstraints().addAll(col1, col2);

        columns.add(left, 0, 0);
        columns.add(right, 1, 0);

        // VBox for header and grid pane
        VBox root = new VBox();
        root.getChildren().addAll(header, columns);

        // Main scene
        Scene scene = new Scene(root, 900, 596);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);

        // Alert for closing window despite unsaved changes
        EventHandler<WindowEvent> handler = new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                if (isUnsaved()) {
                    Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation",
                        "Close without saving?", "You have changes that are not written to file yet.");
                    if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                        event.consume();
                    }
                }
            }
        };
        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, handler);

        // Main stage
        stage.setTitle("IdeateFX");
        stage.getIcons().add(new Image("icon.png"));
        stage.show();
    }

    /**
     * Creates and returns UI elements of the header.
     * @return VBox with header elements
     */
    private static VBox getHeader() {

        // Title
        titleLabel = new Label("IdeateFX: A Problem Ideation Platform");
        titleLabel.getStyleClass().add("title");

        // Background
        VBox header = new VBox();
        header.getChildren().addAll(titleLabel);
        header.getStyleClass().addAll("header", "center");

        return header;
    }

    /**
     * Creates and returns UI elements of the left column.
     * @return VBox with left column elements
     */
    private static VBox getLeft() {

        // Question 1 - Problem
        problemLabel = new Label("What is the problem?");
        problemField = new TextArea();
        problemField.setWrapText(true);
        VBox q1 = new VBox();
        q1.getChildren().addAll(problemLabel, problemField);

        // Question 3 - Customer Need
        customerNeedLabel = new Label("How badly does this customer need this problem fixed?");
        customerNeedField = new Slider(1, 10, 1);
        adjustSlider(customerNeedField);
        VBox q3 = new VBox();
        q3.getChildren().addAll(customerNeedLabel, customerNeedField);

        // Question 5 - Known People With Problem
        knownPeopleWithProblemLabel = new Label(
            "How many people do you know who experience this problem?");
        knownPeopleWithProblemField = new TextField();
        knownPeopleWithProblemField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        VBox q5 = new VBox();
        q5.getChildren().addAll(knownPeopleWithProblemLabel, knownPeopleWithProblemField);

        // Question 6 - Target Market Size
        targetMarketSizeLabel = new Label("How big is the target market?");
        targetMarketSizeField = new TextField();
        targetMarketSizeField.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        VBox q6 = new VBox();
        q6.getChildren().addAll(targetMarketSizeLabel, targetMarketSizeField);

        // Question 7 - Competitors
        competitorsLabel = new Label("Who are the competitors / existing solutions?");
        competitorsField = new TextArea();
        competitorsField.setWrapText(true);
        VBox q7 = new VBox();
        q7.getChildren().addAll(competitorsLabel, competitorsField);

        // Validates form input and adds idea to list if valid
        Button addToList = new Button();
        addToList.setText("Add Idea");
        addToList.setOnAction(event -> {
                addIdea(problemField, targetCustomerField, customerNeedField, feasibilityField,
                    knownPeopleWithProblemField, targetMarketSizeField, competitorsField);
            }
        );

        // Writes current state of the list to ideas.txt file
        Button writeToFile = new Button();
        writeToFile.setText("Save File");
        writeToFile.setOnAction(event -> {
                MediaPlayer player = new MediaPlayer(success);
                player.play();
                FileUtil.saveIdeasToFile(ideas, file);
            }
        );

        // Resets form fields, clears list, and deletes file
        Button reset = new Button();
        reset.setText("Reset");
        reset.setOnAction(event -> {
                Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation",
                    "Are you sure?", "This will reset form, clear list, and delete file.");
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (file.isFile()) {
                        file.delete();
                    }
                    ideas.clear();
                    clearFormFields();
                }
            }
        );

        // HBox for the three form buttons
        HBox formButtons = new HBox();
        formButtons.getChildren().addAll(addToList, writeToFile, reset);
        formButtons.setSpacing(15);
        formButtons.getStyleClass().add("center");

        // VBox for entire left column
        VBox left = new VBox();
        left.getChildren().addAll(q1, q3, q5, q6, q7, formButtons);
        left.getStyleClass().add("column");

        return left;
    }

    /**
     * Creates and returns UI elements of the right column.
     * @return VBox with right column elements
     */
    private static VBox getRight() {

        // Question 2 - Target Customer
        targetCustomerLabel = new Label("Who is the target customer?");
        targetCustomerField = new TextArea();
        targetCustomerField.setWrapText(true);
        VBox q2 = new VBox();
        q2.getChildren().addAll(targetCustomerLabel, targetCustomerField);

        // Question 4 - Feasibility
        feasibilityLabel = new Label("How feasible is the potential solution to this problem?");
        feasibilityField = new Slider(1, 10, 1);
        adjustSlider(feasibilityField);
        VBox q4 = new VBox();
        q4.getChildren().addAll(feasibilityLabel, feasibilityField);

        // List View to show existing ideas
        ListView ideasListView = new ListView<>(ideas);
        ImageView gearsImage = new ImageView();

        // Gears Image if list view is empty
        gearsImage.setImage(new Image("gears.png"));
        ideasListView.setPlaceholder(gearsImage);

        // Double click on list element to open popup to view and edit
        ideasListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    int selectedIndex = ideasListView.getSelectionModel().getSelectedIndex();
                    if (selectedIndex >= 0) {
                        viewIdea(selectedIndex);
                    }
                }
            }
        );

        Label existingIdeas = new Label("Existing Ideas:");

        // Sorts ideas using score
        Button sortList = new Button();
        sortList.setText("\u25B2");
        sortList.getStyleClass().addAll("sort", "roundbtn");
        sortList.setOnAction(event -> {
                if (sortList.getText().equals("\u25B2")) {
                    Collections.sort(ideas);
                    sortList.setText("\u25BC");
                } else {
                    Collections.sort(ideas, Collections.reverseOrder());
                    sortList.setText("\u25B2");
                }
            }
        );

        // Deletes selected idea
        Button deleteIdea = new Button();
        deleteIdea.setText("\u2326");
        deleteIdea.getStyleClass().addAll("delete", "roundbtn");
        deleteIdea.setOnAction(event -> {
                int selectedIndex = ideasListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex != -1) {
                    Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation",
                        "Are you sure?", "This will delete the selected idea.");
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        ideas.remove(selectedIndex);
                    }
                } else if (ideas.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Error", "Empty List",
                        "The list of ideas is already empty.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Error", "No Idea Selected",
                        "Please select an idea to delete.");
                }
            }
        );

        // Spacing for list view buttons
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        spacer2.setMinWidth(10);

        HBox viewButtons = new HBox();
        viewButtons.getChildren().addAll(existingIdeas, spacer1, sortList, spacer2, deleteIdea);
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // VBox for list view
        VBox listBox = new VBox();
        listBox.getChildren().addAll(viewButtons, ideasListView);

        // VBox for entire right column
        VBox right = new VBox();
        right.getChildren().addAll(q2, q4, listBox);
        right.getStyleClass().add("column");

        return right;
    }

    /**
     * Creates popup window to view and edit details of selected idea
     * @param selectedIndex index of the selected idea
     */
    private static void viewIdea(int selectedIndex) {

        // New stage for popup window
        Stage popupStage = new Stage();

        // Grid pane for popup
        GridPane popupPane = new GridPane();
        popupPane.getStyleClass().add("popup-pane");
        popupPane.setHgap(15);
        popupPane.setVgap(25);

        // Labels for left column
        Label q1 = new Label("Problem: ");
        Label q2 = new Label("Target Customer: ");
        Label q3 = new Label("Customer Need: ");
        Label q4 = new Label("Feasibility: ");
        Label q5 = new Label("Known People With Problem: ");
        Label q6 = new Label("Target Market Size: ");
        Label q7 = new Label("Competition: ");
        q5.setWrapText(true);

        // Form fields with existing values for right column
        TextArea a1 = new TextArea();
        a1.setWrapText(true);
        a1.setText(ideas.get(selectedIndex).getProblem());

        TextArea a2 = new TextArea();
        a2.setWrapText(true);
        a2.setText(ideas.get(selectedIndex).getTargetCustomer());

        Slider a3 = new Slider(1, 10, 1);
        adjustSlider(a3);
        a3.setValue(ideas.get(selectedIndex).getCustomerNeed());

        Slider a4 = new Slider(1, 10, 1);
        adjustSlider(a4);
        a4.setValue(ideas.get(selectedIndex).getFeasibility());

        TextField a5 = new TextField();
        a5.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        a5.setText(String.valueOf(ideas.get(selectedIndex).getKnownPeopleWithProblem()));

        TextField a6 = new TextField();
        a6.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        a6.setText(String.valueOf(ideas.get(selectedIndex).getTargetMarketSize()));

        TextArea a7 = new TextArea();
        a7.setWrapText(true);
        a7.setText(ideas.get(selectedIndex).getCompetitors());

        // Add elements to respective grid boxes
        Label[] q = {q1, q2, q3, q4, q5, q6, q7};
        Node[] a = {a1, a2, a3, a4, a5, a6, a7};

        for (int i = 0; i < 7; i++) {
            GridPane.setValignment(q[i], VPos.TOP);
            popupPane.add(q[i], 0, i);
            popupPane.add(a[i], 1, i);
        }

        // Validates input, creates new entry for idea with updates, deletes old entry
        Button updateIdea = new Button();
        updateIdea.setText("Update Idea");
        updateIdea.setOnAction(event -> {
                addIdea(a1, a2, a3, a4, a5, a6, a7);
                ideas.remove(selectedIndex);
                popupStage.close();
            }
        );
        GridPane.setHalignment(updateIdea, HPos.RIGHT);

        popupPane.add(updateIdea, 1, 7);

        // Popup scene
        Scene popupScene = new Scene(popupPane, 730, 550);
        popupScene.getStylesheets().add("style.css");

        // Popup stage
        popupStage.setScene(popupScene);
        popupStage.setTitle("View Idea");
        popupStage.getIcons().add(new Image("icon.png"));
        popupStage.show();
    }

    /**
     * Adjusts tick label and count of sliders to discrete 1 to 10
     * @param slider reference to slider
     */
    private static void adjustSlider(Slider slider) {
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
    }

    /**
     * Creates alert with uniform style and sound
     * @param type the type of alert
     * @param title text for alert title bar
     * @param headerText text for alert header
     * @param contentText text for alert body
     * @return button type clicked by user (optional)
     */
    private static Optional<ButtonType> showAlert(Alert.AlertType type, String title,
        String headerText, String contentText) {

        // Add text to alert
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Replace OK button with YES button
        if (type == Alert.AlertType.CONFIRMATION) {
            alert.getButtonTypes().remove(ButtonType.OK);
            alert.getButtonTypes().add(ButtonType.YES);
        }

        // Add favicon
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("icon.png"));

        // Add stylesheet
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("style.css");

        // Sound effect
        MediaPlayer player = new MediaPlayer(error);
        player.play();

        return alert.showAndWait();
    }

    /**
     * Takes references to seven fields, validates input, and adds idea to list if valid
     * @param problemRef reference to problem field
     * @param targetCustomerRef reference to target customer field
     * @param customerNeedRef reference to customer need field
     * @param feasibilityRed reference to feasibility field
     * @param knownPeopleWithProblemRef reference to known people with problem field
     * @param targetMarketSizeRef reference to target market size field
     * @param competitorsRef reference to competitors field
     */
    private static void addIdea(TextArea problemRef, TextArea targetCustomerRef,
        Slider customerNeedRef, Slider feasibilityRef, TextField knownPeopleWithProblemRef,
        TextField targetMarketSizeRef, TextArea competitorsRef) {

        // Load input from text fields and clean it up, alert if any field is empty
        try {
            String problem =
                problemRef.getText().toString().trim().replaceAll("\\s*\\n\\s*|\\s*\\t\\s*|\\s{2,}", " ");
            String targetCustomer =
                targetCustomerRef.getText().toString().trim().replaceAll("\\s*\\n\\s*|\\s*\\t\\s*|\\s{2,}", " ");
            String knownPeopleWithProblemString = knownPeopleWithProblemRef.getCharacters().toString().trim();
            String targetMarketSizeString = targetMarketSizeRef.getCharacters().toString().trim();
            String competitors =
                competitorsRef.getText().toString().trim().replaceAll("\\s*\\n\\s*|\\s*\\t\\s*|\\s{2,}", " ");
            if (problem.isEmpty() || targetCustomer.isEmpty() || knownPeopleWithProblemString.isEmpty()
                || targetMarketSizeString.isEmpty() || competitors.isEmpty()) {
                throw new NullPointerException();
            }

            // Parses integer from known people with problem field, alert if non-numeric
            try {
                int knownPeopleWithProblem = Integer.parseInt(knownPeopleWithProblemString.replaceAll(",", ""));

                // Parses integer from target market size field, alert if non-numeric
                try {
                    int targetMarketSize = Integer.parseInt(targetMarketSizeString.replaceAll(",", ""));

                    // Alert if any field negative or if target market size 0
                    if (knownPeopleWithProblem < 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input",
                            "Number of people must be positive.");
                    } else if (targetMarketSize <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input",
                            "Target market size must be positive.");
                    } else {
                        // Success sound effect
                        MediaPlayer player = new MediaPlayer(success);
                        player.play();

                        // Load values from sliders
                        int customerNeed = (int) customerNeedRef.getValue();
                        int feasibility = (int) feasibilityRef.getValue();

                        // Add idea to list
                        StartUpIdea idea = new StartUpIdea(problem, targetCustomer, customerNeed,
                            feasibility, knownPeopleWithProblem, targetMarketSize, competitors);
                        ideas.add(idea);

                        // Reset form fields
                        clearFormFields();
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input",
                        "Target market size must be numeric.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input",
                    "Number of people must be numeric.");
            }
        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Empty Field", "Fields cannot be empty.");
        }
    }

    /**
     * Resets all form fields
     */
    private static void clearFormFields() {
        problemField.clear();
        targetCustomerField.clear();
        customerNeedField.setValue(1);
        feasibilityField.setValue(1);
        knownPeopleWithProblemField.clear();
        targetMarketSizeField.clear();
        competitorsField.clear();
    }

    /**
     * Reads ideas.txt to load existing ideas into list
     * @return initialized list
     */
    private static ObservableList<StartUpIdea> loadFromFile() {

        // Try opening file
        try {
            Scanner input = new Scanner(file);
            String[] values = new String[7];
            ArrayList<StartUpIdea> temp = new ArrayList<>();

            // Read until end of file
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                line = line.substring(0, Math.max(0, line.length() - 1));

                // If line matches a number, begin reading a new idea
                if (line.matches("^[0-9]+$")) {

                    // Add all characters after semicolon to the values list
                    for (int i = 0; i <= 6; i++) {
                        values[i] = input.nextLine().trim();
                        values[i] = values[i].substring(values[i].indexOf(':') + 2, values[i].length());
                    }

                    // Create idea from values and add to array list
                    temp.add(new StartUpIdea(values[0], values[1], Integer.parseInt(values[2]),
                        Integer.parseInt(values[3]), Integer.parseInt(values[4]),
                        Integer.parseInt(values[5]), values[6]));
                }
            }

            // Return observable list with ideas
            return FXCollections.observableArrayList(temp);

        } catch (Exception e) {

            // Return blank observable list
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Helper function to check if there are unsaved changes by comparing file and list
     * @return true if unsaved changes, false if none
     */
    private boolean isUnsaved() {
        try {
            ObservableList<StartUpIdea> ideasFile = loadFromFile();
            if (ideasFile.size() != ideas.size()) {
                return true;
            }
            for (int i = 0; i < ideasFile.size(); i++) {
                if (!ideas.get(i).equals(ideasFile.get(i))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}