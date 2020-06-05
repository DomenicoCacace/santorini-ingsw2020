package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.InputDialog;
import it.polimi.ingsw.view.cli.console.graphics.components.ReturningListItem;
import it.polimi.ingsw.view.cli.console.graphics.components.Toggleable;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;

import java.util.List;

/**
 * A dialog showing a list of elements; when the enter key is pressed, the currently selected item is triggered, causing
 * it to return its value to the inputManager and this to be closed
 */
public class SingleChoiceListDialog extends InputDialog {

    /**
     * Default constructor
     * <p>
     * Creates a SimpleListChoiceDialog, which dimensions are adapted on the options provided;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     * @param options the options to show
     */
    public SingleChoiceListDialog(String title, String message, Window caller, List<String> options) {
        super(title, message, width(options), 8 + options.size(), caller);

        int colOff = findCenter(this.getWidth(), options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));
        options.forEach(s -> addInteractiveItem(new ReturningListItem(this, new CursorPosition(7 + options.indexOf(s), colOff), s, options.indexOf(s) + 1)));
    }

    /**
     * Custom constructor
     * <p>
     * Creates a SimpleListChoiceDialog, using custom
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     * @param options the options to show
     * @param initCoord the initial coordinates
     */
    public SingleChoiceListDialog(String title, String message, Window caller, List<String> options, int width, int height, CursorPosition initCoord) {
        super(title, message, width, height, initCoord, caller);

        int colOff = findCenter(this.getWidth(), options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));
        options.forEach(s -> addInteractiveItem(new ReturningListItem(this, new CursorPosition(7 + options.indexOf(s), colOff), s, options.indexOf(s) + 1)));
    }

    /**
     * Determines the dialog width based on the number of options
     *
     * @param options the list of options
     * @return the adapted dialog size
     */
    protected static int width(List<String> options) {
        return Math.max(50, options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0) + 8);
    }


}
