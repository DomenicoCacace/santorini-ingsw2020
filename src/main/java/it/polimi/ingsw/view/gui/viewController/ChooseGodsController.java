package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.gui.utils.ResizableImageView;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChooseGodsController {

    private GUI gui;

    @FXML
    private VBox godImageContainer;

    private final List<ResizableImageView> enabledGods = new ArrayList<>();

    public VBox getGodImageContainer() {
        return godImageContainer;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void cardClicked(MouseEvent event) {
        gui.setInputString(String.valueOf(((ResizableImageView) event.getPickResult().getIntersectedNode()).getIndex() +1));
        for(ResizableImageView resizableImageView : enabledGods){
            resizableImageView.setDisable(true);
        }
        ((ResizableImageView) event.getSource()).setOpacity(0.2);
    }

    public void addEnabledGod(ResizableImageView resizableImageView){
        enabledGods.add(resizableImageView);
    }
}



