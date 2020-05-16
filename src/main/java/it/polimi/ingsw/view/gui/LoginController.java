package it.polimi.ingsw.view.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

    private String username;
    private String ipAddress;
    @FXML
    private TextField usernameID;
    @FXML
    private TextField ipID;

    private GUI gui;

    public void setGUI(GUI gui){
        this.gui = gui;
        usernameID.focusedProperty().addListener((observable, oldValue, newValue) -> gui.setInputString(usernameID.getText()));
        ipID.focusedProperty().addListener((observable, oldValue, newValue) -> gui.setInputString(ipID.getText()));
    }

    @FXML
    public void onLogin(){}

}
