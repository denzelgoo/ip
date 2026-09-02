package bro.ui;

import bro.Bro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bro bro;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image broImage = new Image(this.getClass().getResourceAsStream("/images/DaBro.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bro instance and displays the initial welcome greeting.
     *
     * @param bro The Bro chatbot instance.
     */
    public void setBro(Bro bro) {
        this.bro = bro;
        dialogContainer.getChildren().addAll(
                DialogBox.getBroDialog(bro.getWelcome(), broImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Bro's reply,
     * and then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bro.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBroDialog(response, broImage)
        );
        userInput.clear();
    }
}
