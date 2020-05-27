package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * InputManager abstraction
 * <p>
 * An InputManager is an object responsible for the console input acquisition and redirection
 */
public abstract class InputManager {
    //public static final String Constants.QUIT = "quit";

    /**
     * The scheduled task that manage the timeout on user input
     */
    protected static ScheduledFuture<?> inputCountdown;
    private int secondsPassed = 0;
    /**
     * The client to manage the input for
     */
    protected final Client client;
    /**
     * The view to call methods on
     */
    protected final ViewInterface view;
    /**
     * Determines if an eventual input has to be discarded or not
     */
    protected boolean isWaitingForInput = true;

    /**
     * Default constructor
     *
     * @param client the client to manage the inputs for
     */
    public InputManager(Client client) {
        this.client = client;
        this.view = client.getView();
        this.isWaitingForInput = true;
    }

    /**
     * Determines how to handle the received input based on the internal state
     *
     * @param input the user input
     */
    public abstract void manageInput(String input);

    /**
     * <a href=https://xkcd.com/327/>Sanitizes</a> an input string
     *
     * @param input the user input
     * @return a sanitized version of the input string
     */
    protected String cleanInput(String input) {
        return input.replace(" ", "").replace("\t", "");
    }


    public void setWaitingForInput(boolean waitingForInput) {
        isWaitingForInput = waitingForInput;
    }

    /**
     *
     * @param availableTime the maximum time the user has to input something
     */
    public void startTimer(int availableTime) {
        secondsPassed = 0;
        if (inputCountdown != null) {
            inputCountdown.cancel(true);
        }
        ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(2);
        inputCountdown = ex.scheduleAtFixedRate(() -> increaseSecondPassed(availableTime), 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Method called by the scheduled future, it increase and manage the secondPassed variable
     * when secondPassed reaches availableTime the user will be disconnected for inactivity
     * @param availableTime the maximum time the user has to input something
     */
    private void increaseSecondPassed(int availableTime) {
        if (secondsPassed == availableTime) {
            view.showErrorMessage("Timeout!!");
            client.inputTimeout();
            stopTimer();
        } else if (secondsPassed == (availableTime - 30)) {
            view.showErrorMessage("You have only 30 seconds left to insert a valid command!");
            secondsPassed++;
        } else
            secondsPassed++;
    }

    /**
     * This method cancels the task and reset the second passed
     */
    public void stopTimer() {
        if (inputCountdown != null) {
            inputCountdown.cancel(true);
        }
        secondsPassed = 0;
    }
}
