package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;

/**
 * InputManager abstraction
 * <p>
 *     An InputManager is an object responsible for the
 * </p>
 */
public abstract class InputManager {

    /**
     * Determines if an eventual input has to be discarded or not
     */
    protected boolean isWaitingForInput = true;

    /**
     * The client to manage the input for
     */
    protected final Client client;

    /**
     * The view to call methods on
     */
    protected final ViewInterface view;

    /**
     * Default constructor
     * @param client the client to manage the inputs for
     */
    public InputManager(Client client) {
        this.client = client;
        this.view = client.getView();
        this.isWaitingForInput = true;
    }

    /**
     * Determines how to handle the received input based on the internal state
     * @param input the user input
     */
    public abstract void manageInput(String input);

    /**
     * <a href=https://xkcd.com/327/>Sanitizes</a> an input string
     *
     * @param input the user input
     * @return a sanitized version of the input string
     */
    protected String cleanInput(String input){
        return input.replace(" ", "").replace("\t", "");
    }


    public void setWaitingForInput(boolean waitingForInput) {
        isWaitingForInput = waitingForInput;
    }
}
