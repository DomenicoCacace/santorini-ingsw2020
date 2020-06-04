package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.ClosingButton;
import it.polimi.ingsw.view.cli.console.graphics.components.InputDialog;
import it.polimi.ingsw.view.cli.console.graphics.components.TextBox;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;


public final class TextInputDialog extends InputDialog {

    /**
     * Default constructor
     * <p>
     * Creates a standard size textInputDialog window, using the properties file, without printing it;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one.
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     * @param fields  the input fields required
     */
    public TextInputDialog(String title, String message, Window caller, int width, int height, String... fields) {
        super(title, message, width, height, caller);
        int colOff = findCenter(this.getWidth(), width - 4);
        for (int i = 0; i < fields.length; i++)
            addInteractiveItem(new TextBox(this, new CursorPosition(7 + 5 * i, colOff), width - 4, 3, fields[i]));
        int rowOff = findCenter(this.getHeight(), 3) * 9 / 5;
        colOff = findCenter(this.getWidth(), width / 2);
        addInteractiveItem(new ClosingButton(this, new CursorPosition(rowOff, colOff), width / 2, 3, "OK"));
    }
}
