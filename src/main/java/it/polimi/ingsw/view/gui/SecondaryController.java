package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;

import java.io.IOException;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        GuiInit.setRoot("test");
    }
}