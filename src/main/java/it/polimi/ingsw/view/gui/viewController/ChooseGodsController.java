package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.gui.ResizableImageView;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChooseGodsController {
    private GUI gui;
    private boolean inputRequested = false;

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void setInputRequested(boolean inputRequested) {
        this.inputRequested = inputRequested;
    }
    public void cardClicked(MouseEvent event) {
        if(inputRequested) {
            gui.setInputString((event.getPickResult().getIntersectedNode().getId()));
            ((ResizableImageView) event.getSource()).setDisable(true);
            ((ResizableImageView) event.getSource()).setOpacity(0.2);
            inputRequested=false;
        }
    }
}
