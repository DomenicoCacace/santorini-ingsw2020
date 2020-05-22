package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class LobbyController {

    private static boolean inputRequested = false;
    public ListView<String> lobbyList;
    public Button backButton;
    private boolean joined = false;
    private GUI gui;
    @FXML
    private Button createBtn;
    @FXML
    private Button joinBtn;

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
        if(inputRequested) {
            createBtn.setDisable(true);
            joinBtn.setDisable(true);
            inputRequested=false;
            gui.setInputString("Create lobby");
        }
    }

    public void onJoin() {
        if (inputRequested) {
            createBtn.setDisable(true);
            joinBtn.setDisable(true);
            inputRequested = false;
            joined = true;
            gui.setInputString("Join lobby");
        }
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void onLobbySelected() {
        if (inputRequested) {
            gui.setInputString(String.valueOf(lobbyList.getSelectionModel().getSelectedIndex()- 1));
            lobbyList.setDisable(true);
        }
    }


}
