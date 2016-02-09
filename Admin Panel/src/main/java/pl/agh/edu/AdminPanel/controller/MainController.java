package pl.agh.edu.AdminPanel.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.agh.edu.AdminPanel.utils.DatabaseOperations;

public class MainController {

    @FXML
    private ChoiceBox<ActionType> actionChoiceBox;
    @FXML
    private Label firstLabel;
    @FXML
    private Label secondLabel;
    @FXML
    private TextField firstTextField;
    @FXML
    private TextField secondTextField;
    @FXML
    private Button actionButton;
    @FXML
    private VBox secondField;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        actionChoiceBox.getItems().setAll(ActionType.values());
        actionChoiceBox.setValue(ActionType.NEW_USER);
        actionChoiceBox.setOnAction(this::adjustControls);
        actionButton.setOnAction(this::newUserAction);
    }

    @FXML
    private void newUserAction(ActionEvent event) {
        String userName = firstTextField.getText();
        String password = secondTextField.getText();
        boolean success = DatabaseOperations.getInstance().insertNewUserIfNotExists(userName, password);
        setStatusLabel(success);
    }

    @FXML
    private void newGroupAction(ActionEvent event) {
        String groupName = firstTextField.getText();
        boolean success = DatabaseOperations.getInstance().insertNewGroupIfNotExists(groupName);
        setStatusLabel(success);
    }

    @FXML
    private void userToGroupAction(ActionEvent event) {
        String userName = firstTextField.getText();
        String groupName = secondTextField.getText();
        boolean success = DatabaseOperations.getInstance().addUserToGroup(userName, groupName);
        setStatusLabel(success);
    }

    private void setStatusLabel(boolean success) {
        statusLabel.setVisible(true);
        if (success) {
            statusLabel.setText("action successful");
            statusLabel.setStyle("-fx-text-fill: green; -fx-padding: 10px;");
        } else {
            statusLabel.setText("error when performing action");
            statusLabel.setStyle("-fx-text-fill: red; -fx-padding: 10px;");
        }
    }

    private void adjustControls(ActionEvent actionEvent) {
        firstTextField.setText("");
        secondTextField.setText("");
        statusLabel.setVisible(false);
        switch (actionChoiceBox.getValue()) {
            case NEW_USER:
                firstLabel.setText("username");
                secondLabel.setText("password");
                secondField.setVisible(true);
                actionButton.setOnAction(this::newUserAction);
                break;
            case NEW_GROUP:
                firstLabel.setText("group name");
                secondField.setVisible(false);
                actionButton.setOnAction(this::newGroupAction);
                break;
            case USER_TO_GROUP:
                firstLabel.setText("username");
                secondLabel.setText("group name");
                secondField.setVisible(true);
                actionButton.setOnAction(this::userToGroupAction);
                break;
        }
    }
}
