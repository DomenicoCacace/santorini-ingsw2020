package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.gui.utils.ResizableImageView;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ChooseGodsController {

    private GUI gui;
    @FXML
    private HBox godImageContainer;

    public HBox getGodImageContainer() {
        return godImageContainer;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void cardClicked(MouseEvent event) {
        gui.setInputString(String.valueOf(((ResizableImageView) event.getPickResult().getIntersectedNode()).getIndex() +1));
        ((ResizableImageView) event.getSource()).setDisable(true);
        ((ResizableImageView) event.getSource()).setOpacity(0.2);
    }
}



