package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.Constants;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ReloadMatchController {

    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    private GUI gui;

    public void onReloadMatch(MouseEvent mouseEvent){
        if(mouseEvent.getSource().equals(yesButton)){
            gui.setInputString(Constants.YES);
        }
        else if (mouseEvent.getSource().equals(noButton)){
            gui.setInputString(Constants.NO);
        }
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }
}
