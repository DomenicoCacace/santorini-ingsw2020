package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

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
        gui.setInputString(String.valueOf(playersList.getSelectionModel().getSelectedIndex()+ 1));
        playersList.setDisable(true);
    }
}
