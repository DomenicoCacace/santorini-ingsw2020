package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.Constants;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class LobbyController {

    private static boolean inputRequested = false;
    private GUI gui;
    private String lobbyName = "";
    private String lobbySize = "";
    @FXML
    private Button createBtn;
    @FXML
    private Button joinBtn;
    @FXML
    private ListView<String> lobbyList;
    @FXML
    private Button backButton;
    @FXML
    private TextField sizeTextField;
    @FXML
    private TextField nameTextField;

    public static void setInputRequested(boolean inputRequested) {
        LobbyController.inputRequested = inputRequested;
    }

    public Button getJoinBtn() {
        return joinBtn;
    }

    public ListView<String> getLobbyList() {
        return lobbyList;
    }

    public void onCreate() {
        if (inputRequested && !(lobbyName.isBlank() || lobbySize.isBlank())) {
            inputRequested = false;
            gui.setInputString(Constants.CREATE_LOBBY);
            gui.setInputString(lobbyName);
            gui.setInputString(lobbySize);
            createBtn.setDisable(true);
            joinBtn.setDisable(true);
        }
    }

    public void enableCreateButton() {
        if (this.createBtn.isDisable() && !(lobbySize.equals("3") || lobbySize.equals("2"))) {
            this.createBtn.setDisable(false);
            this.createBtn.setOnMouseClicked(event -> {
                this.createBtn.setDisable(true);
                gui.setInputString(lobbySize);
            });
            inputRequested = true;
        }
    }

    public void onJoin() {
        if (inputRequested) {
            createBtn.setDisable(true);
            joinBtn.setDisable(true);
            inputRequested = false;
            gui.setInputString(Constants.JOIN_LOBBY);
        }
    }

    public void backButton() {
        gui.setInputString(String.valueOf(lobbyList.getItems().size() + 1));
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
        if (nameTextField != null && sizeTextField != null) {
            nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> this.lobbyName = nameTextField.getText());
            sizeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> this.lobbySize = sizeTextField.getText());
        }
    }

    public void onLobbySelected() {
        if (inputRequested && lobbyList.getSelectionModel().getSelectedItem() != null) {
            gui.setInputString(String.valueOf(lobbyList.getSelectionModel().getSelectedIndex() + 1));
            lobbyList.setDisable(true);
            backButton.setDisable(true);
        }
    }


}
