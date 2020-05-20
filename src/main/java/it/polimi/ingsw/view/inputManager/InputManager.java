package it.polimi.ingsw.view.inputManager;

public abstract class InputManager {

    protected boolean isWaitingForInput = true;

    public abstract void manageInput(String string);

    protected String cleanInput(String input){
        return input.replace(" ", "").replace("\t", "");
    }

    public void setWaitingForInput(boolean waitingForInput) {
        isWaitingForInput = waitingForInput;
    }
}
