package it.polimi.ingsw.client.view.gui.viewController;

import it.polimi.ingsw.client.view.Constants;
import it.polimi.ingsw.client.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoginController {

    private static final Lock lock = new ReentrantLock();
    @FXML
    public Button loginBtn;
    private GUI gui;
    private String username = "";
    private String ipAddress = "";
    private boolean ipIsSet = false;
    private boolean isReloading;
    @FXML
    private TextField usernameID;
    @FXML
    private TextField ipID;
    @FXML
    private ListView<String> oldConfigs;

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
            if (!this.username.isBlank() && isReloading) {
                gui.setInputString(Constants.NO);
                isReloading = false;
                oldConfigs.setDisable(true);
                oldConfigs.setOpacity(0.2);
            }
        });
        ipID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.ipAddress = ipID.getText();
            if (!this.ipAddress.isBlank() && isReloading) {
                gui.setInputString(Constants.NO);
                isReloading = false;
                oldConfigs.setDisable(true);
                oldConfigs.setOpacity(0.2);
            }
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
            gui.setInputString(Constants.NO);
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
        if (isReloading && oldConfigs.getSelectionModel().getSelectedItem() != null) {
            gui.setInputString(Constants.YES);
            gui.setInputString(String.valueOf(oldConfigs.getSelectionModel().getSelectedIndex() + 2));
        }
        lock.unlock();
    }


    public void showGameRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.GAME_RULES);
        alert.showAndWait();
    }
}

