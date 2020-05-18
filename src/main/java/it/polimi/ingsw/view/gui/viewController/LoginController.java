package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.view.gui.GUI;
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
    private static boolean ipRequested;
    private static boolean usernameRequested;
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

    public void setIpIsSet(boolean ipIsSet) {
        this.ipIsSet = ipIsSet;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
        usernameID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.username = usernameID.getText();
            //oldConfigs.setDisable(!(this.username.isBlank() && this.ipAddress.isBlank()));
        });
        ipID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.ipAddress = ipID.getText();
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
        if ((!ipAddress.isBlank() || ipIsSet) && !username.isBlank()) {
            lock.lock();
            try {
                if (!ipIsSet) {
                    while (!ipRequested)
                        condition.await();
                    gui.setInputString(ipAddress);
                    ipAddress = "";
                    ipRequested = false;
                }
                while (!usernameRequested)
                    condition.await();
                gui.setInputString(username);
                username = "";
                usernameRequested = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            loginBtn.setDisable(true);
        }
    }

    public static void requestIP() {
        lock.lock();
        ipRequested = true;
        condition.signalAll();
        lock.unlock();
    }

    public static void requestUsername() {
        lock.lock();
        usernameRequested = true;
        condition.signalAll();
        lock.unlock();
    }

    public void onOldConfigSelected() {
        lock.lock();
        try {
            while (!ipRequested)
                condition.await();
            String input = oldConfigs.getSelectionModel().getSelectedItem();
            username = input.substring(0, input.indexOf(" -- "));
            username = username.trim();
            ipAddress = input.substring(input.indexOf(" -- ") + 4);
            ipAddress = ipAddress.trim();
            oldConfigs.setDisable(true);
            onLogin();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

