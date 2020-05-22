package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.inputManagers.LoginManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoginController {

    private GUI gui;
    private String username = "";
    private String ipAddress = "";
    private boolean ipIsSet = false;
    private boolean isReloading;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    @FXML
    private TextField usernameID;
    @FXML
    private TextField ipID;
    @FXML
    private ListView<String> oldConfigs;
    @FXML
    public Button loginBtn;

    public void setReloading(boolean reloading) {
        isReloading = reloading;
    }

    public void setIpIsSet(boolean ipIsSet) {
        this.ipIsSet = ipIsSet;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
        usernameID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.username = usernameID.getText();
            if(!this.username.isBlank()) {
                if (isReloading) {
                    gui.setInputString(CLI.NO);
                    isReloading = false;
                    oldConfigs.setDisable(true);
                    oldConfigs.setOpacity(0.2);
                }
            }


            //oldConfigs.setDisable(!(this.username.isBlank() && this.ipAddress.isBlank()));
        });
        ipID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.ipAddress = ipID.getText();
            if(!this.ipAddress.isBlank()) {
                if (isReloading) {
                    gui.setInputString(CLI.NO);
                    isReloading = false;
                    oldConfigs.setDisable(true);
                    oldConfigs.setOpacity(0.2);
                }
            }
            //oldConfigs.setDisable(!(this.username.isBlank() && this.ipAddress.isBlank()));
        });
    }

    public TextField getUsernameID() {
        return usernameID;
    }

    public TextField getIpID() {
        return ipID;
    }

    public ListView<String> getOldConfigs() {
        return oldConfigs;
    }

    public Button getLoginBtn() {
        return loginBtn;
    }

    @FXML
    public void onLogin() {
        lock.lock();
        if (isReloading)
            gui.setInputString(CLI.NO);
        if ((!ipAddress.isBlank() || ipIsSet) && !username.isBlank()) {
            if (!ipIsSet) {
                gui.setInputString(ipAddress);
                ipAddress = "";
            }
            gui.setInputString(username);
            username = "";
            loginBtn.setDisable(true);
            lock.unlock();
        }
    }

    public void onOldConfigSelected() {
        lock.lock();
        if(isReloading) {
            gui.setInputString(CLI.YES);
            gui.setInputString(String.valueOf(oldConfigs.getSelectionModel().getSelectedIndex()+2));
        }
        lock.unlock();
    }


}

