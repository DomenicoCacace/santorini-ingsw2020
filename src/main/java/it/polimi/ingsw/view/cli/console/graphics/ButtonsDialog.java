package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.InputDialog;
import it.polimi.ingsw.view.cli.console.graphics.components.ActiveItem;
import it.polimi.ingsw.view.cli.console.graphics.components.ReturningButton;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An InputDialog with one or two clickable buttons, each of them sending a value to the input manager
 */
public final class ButtonsDialog extends InputDialog {

    /**
     * Default constructor
     * <p>
     * Creates a standard size inputDialog window, using the properties file, without printing it;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one.
     * <br>
     *
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title        the dialog title
     * @param message      the dialog message
     * @param caller       the window which invoked this
     * @param buttons      a map containing the Button text/Return value combos
     * @param stackButtons a flag which, if true, makes the dialog print the buttons like <i>in a stack</i>
     */
    public ButtonsDialog(String title, String message, Window caller, HashMap<String, String> buttons, boolean stackButtons) {
        super(title, message, 30, calculateHeight(stackButtons, buttons.keySet().size()), caller);

        /*buttonText = buttons.keySet().toArray()[0].toString();*/
        int numOfButtons = buttons.keySet().size();
        int buttonWidth = ActiveItem.maxStringLength(new ArrayList<>(buttons.keySet())) + 4;

        if (stackButtons || numOfButtons > 2) {
            int colOff = findCenter(this.getWidth(), buttonWidth);
            int rowOff = 5;
            buttons.forEach((t, r) -> {
                int actualRowOff = rowOff + 5 * new ArrayList<>(buttons.keySet()).indexOf(t);
                addInteractiveItem(new ReturningButton(this, new CursorPosition(actualRowOff, colOff), buttonWidth, 3, t, r));
            });
        } else {
            int colOff = findCenter(this.getWidth() / numOfButtons, buttonWidth);
            int rowOff = findCenter(this.getHeight(), 3) * 9 / 5;
            String buttonText = buttons.keySet().toArray()[0].toString();
            addInteractiveItem(new ReturningButton(this, new CursorPosition(rowOff, colOff), buttonWidth, 3, buttonText, buttons.get(buttonText)));
            if (numOfButtons > 1) {
                colOff += getWidth() / 2;
                buttonText = buttons.keySet().toArray()[1].toString();
                addInteractiveItem(new ReturningButton(this, new CursorPosition(rowOff, colOff), buttonWidth, 3, buttonText, buttons.get(buttonText)));
            }

        }
    }

    private static int calculateHeight(boolean buttonsAreStacked, int numberOfButtons) {
        if (buttonsAreStacked)
            return 5 * (numberOfButtons + 1);
        else
            return 12;
    }


}
