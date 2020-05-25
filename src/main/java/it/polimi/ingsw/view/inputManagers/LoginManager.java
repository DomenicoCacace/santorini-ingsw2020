package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.cli.CLI;

import java.util.ArrayList;
import java.util.List;

public class LoginManager extends InputManager {

    private final List<String> savedConfigs = new ArrayList<>();
    private boolean wantsToLoadSetting;
    private boolean isIpAlreadySet = false;

    public LoginManager(Client client, List<String> savedConfigs) {
        super(client);
        this.savedConfigs.addAll(savedConfigs);
    }

    public LoginManager(Client client, boolean isIpAlreadySet) {
        super(client);
        this.isIpAlreadySet = isIpAlreadySet;
    }

    /**
     * This method is called by the scanner when it receives an input and the InputManager parameter set inside the viewInterface class is a LoginManager object;
     * When the client calls one of the login related methods it will also set the view's InputManager as a LoginManager.
     * The Manager that is set inside the view class represents the "state" of our program, we can set it through an enum or with a standard setter from Client / MessageParser
     * This method is called inside a synchronized block to avoid being called while the other thread is setting inputManager with a different obj.
     *
     * @param input is the input of the user
     */
    @Override
    public synchronized void manageInput(String input) {
        if (isWaitingForInput) {
            if (savedConfigs.size() > 0) { //If there are saved configs saved locally
                if (!wantsToLoadSetting) { //If it's false but there are saved configs than the user had to input y or n
                    chooseToReloadSetting(input);
                } else {
                    try {
                        input = cleanInput(input);
                        int index = Integer.parseInt(input) - 1;
                        loadSavedConfig(index);
                    } catch (NumberFormatException e) {
                        generateOptions(); //same thing if he answers with a letter
                    }
                }
            } else if (!isIpAlreadySet) { //If the player doesn't want to load any settings the next input will be his ip
                isIpAlreadySet = true;
                client.setIpAddress(input);
                view.askUsername();
            } else { //after he chooses the ip his next input will be the username
                isWaitingForInput = false;
                client.setUsername(input);
            }
        }
    }

    private void loadSavedConfig(int index) {
        if (!(index < 0 || index > (savedConfigs.size() / 2))) {
            if (index != 0) { //If index == 0 the player chose to manually input user/Ip
                stopTimer();
                index--;
                index = index * 2;
                isWaitingForInput = false;
                client.setIpAddress(savedConfigs.get(index));
                client.setUsername(savedConfigs.get(index + 1));
                //client.setLoginData(savedConfigs.get(index), savedConfigs.get(index+1)); //New method, I pass to him both user and ip
            } else {
                view.askIP();
                savedConfigs.clear(); //If he answered " insert ip manually " i clear the savedCinfigs so that he want be able to load them
                startTimer(60);
            }
        } else {
            view.showErrorMessage("Insert a valid option");
            generateOptions(); //if he answers with a number that it'not on the list we print the options again
        }
    }

    private void chooseToReloadSetting(String input) {
        input = cleanInput(input);
        if (input.equals(CLI.YES)) { //If he answers yes than i print all the saved configs, the next input will have to be an integer
            stopTimer();
            wantsToLoadSetting = true;
            generateOptions(); //This method prints the saved configs with the format user -- ipAddress
        } else if (input.equals(CLI.NO)) {
            stopTimer();
            view.askIP();
            savedConfigs.clear(); //If he doesn't want to reload setting i clear the list so that the next input will be managed without passing throught this if
            startTimer(60);
        } else {
            stopTimer();
            view.askToReloadLastSettings(savedConfigs); //If he doesn't answer with yes or no i repeat the question
            startTimer(60);
        }
    }

    public void generateOptions() {
        List<String> configOptions = new ArrayList<>();
        configOptions.add("Use a different username/IP instead!");
        for (int i = 1; i <= savedConfigs.size(); i += 2) {
            configOptions.add(savedConfigs.get(i) + " -- " + savedConfigs.get(i - 1)); //This convert
        }
        view.printOptions(configOptions);
    }
}
