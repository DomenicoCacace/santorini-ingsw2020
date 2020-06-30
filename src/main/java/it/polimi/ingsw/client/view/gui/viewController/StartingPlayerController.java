package it.polimi.ingsw.client.view.gui.viewController;

import it.polimi.ingsw.client.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class StartingPlayerController {
    @FXML
    private ListView<String> playersList;
    private GUI gui;

    public ListView<String> getPlayersList() {
        return playersList;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void onPlayerSelected() {
        if (playersList.getSelectionModel().getSelectedItem() != null) {
            gui.setInputString(String.valueOf(playersList.getSelectionModel().getSelectedIndex() + 1));
            playersList.setDisable(true);
        }
    }
}
