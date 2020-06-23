package it.polimi.ingsw.view.cli.console;

import it.polimi.ingsw.view.Constants;
import it.polimi.ingsw.view.cli.console.printers.Printer;

public class CursorPosition implements KeyEventListener {

    public static final CursorPosition maxCursor = new CursorPosition(Printer.SCENE_HEIGHT, Printer.SCENE_WIDTH);
    private static final CursorPosition homePosition = new CursorPosition();
    private int row;
    private int col;

    public CursorPosition() {
        this.row = 0;
        this.col = 0;
    }

    public CursorPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static CursorPosition offset(CursorPosition pos, int rowOff, int colOff) {
        return new CursorPosition(pos.getRow() + rowOff, pos.getCol() + colOff);
    }

    public void setCoordinates(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setAsHome() {
        homePosition.row = row;
        homePosition.col = col;
    }

    public void moveToHome() {
        this.setCoordinates(homePosition.getRow(), homePosition.getCol());
        moveCursorTo();
    }

    public void moveCursorTo() {
        System.out.print((String.format("\033[%d;%dH", this.row, this.col)));
    }

    public void blink() {
        if (Console.currentWindow() != null)
            System.out.print(Constants.BLINKER + Console.currentWindow().getTextColor());
    }

    public void stopBlink() {
        if (Console.currentWindow() != null)
            System.out.print(Constants.STOP_BLINK + Console.currentWindow().getTextColor() + " ");
    }


    @Override
    public void onPrintableKey(char key) {
        if (Console.in.isConsoleInputEnabled()) {
            Console.out.echo(key);

            if (col <= maxCursor.col)
                col++;
            else {
                col = homePosition.col;
                row++;
            }
            blink();
        }
    }

    @Override
    public void onCarriageReturn() {
        if (Console.in.isConsoleInputEnabled() && Console.in.currentBufferSize() > 0) {
            stopBlink();
            col = homePosition.col;
            setAsHome();
            moveToHome();
        }
    }

    @Override
    public void onBackspace() {
        if (Console.in.isConsoleInputEnabled()) {
            stopBlink();
            Console.out.del();
            if (col > homePosition.col) {
                col--;
            }
            moveCursorTo();
            blink();
        }
    }
}