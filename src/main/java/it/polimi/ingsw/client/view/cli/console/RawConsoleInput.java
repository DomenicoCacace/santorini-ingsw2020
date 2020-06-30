package it.polimi.ingsw.client.view.cli.console;

import it.polimi.ingsw.client.view.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


/**
 * Console input utilities
 */
public class RawConsoleInput {

    private final Semaphore semaphore;
    private final List<KeyEventListener> keyEventListeners;
    private final int maxBufferSize;
    private boolean escape;
    private StringBuilder inputBuffer;
    private boolean inputEnable;


    public RawConsoleInput(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
        keyEventListeners = new ArrayList<>();
        inputBuffer = new StringBuilder();
        semaphore = new Semaphore(0);
        inputEnable = false;
    }

    /**
     * Constantly listens for keystrokes
     */
    protected void listenForRawInput() {
        int rawInput;
        while (true) {
            try {
                rawInput = System.in.read();
                if (escape)
                    handleEscapeInputs(rawInput);
                else
                    handleInputs(rawInput);
            } catch (Exception e) {
                Console.close();
                break;
            }
        }
    }

    protected void listenForStandardInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            inputBuffer = new StringBuilder(scanner.nextLine());
            semaphore.release();
        }
    }

    public String getCurrentBuffer() {
        return inputBuffer.toString();
    }

    public void flushBuffer() {
        inputBuffer = new StringBuilder();
        semaphore.drainPermits();
    }

    /**
     * Returns the last line read and empties the buffer
     *
     * @return the next line
     */
    public String nextLine() {
        try {
            semaphore.acquire();
            String line = inputBuffer.toString();
            inputBuffer = new StringBuilder();
            semaphore.drainPermits();
            return line;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Console.close();
            return null;
        }
    }

    /**
     * Returns true if this scanner has another token in its input.
     *
     * @return true if and only if this scanner has another token
     */
    public boolean hasNext() {
        return semaphore.availablePermits() > 0;
    }

    /**
     * Provides the number of printable characters in the buffer
     *
     * @return the number of characters in the buffer
     */
    public int currentInputSize() {
        return inputBuffer.length();
    }

    /**
     * Handles non-escaped input
     *
     * @param input the raw user input
     */
    private void handleInputs(int input) {

        // To be always evaluated
        switch (input) {
            case Constants.END_OF_TEXT:  // CTRL + C
                Console.close();
                break;
            case Constants.ESCAPE:      // ESC
                escape = true;
                break;
            case Constants.CARRIAGE_RETURN:
                for (KeyEventListener l : keyEventListeners)
                    l.onCarriageReturn();
                if (Console.in.currentBufferSize() > 0)
                    semaphore.release();
                break;
            case Constants.HORIZONTAL_TAB:
                for (KeyEventListener l : keyEventListeners)
                    l.onTab();
                break;
            default:
                break;
        }

        // To be evaluated only when the console is enabled
        if (isConsoleInputEnabled()) {
            switch (input) {
                case Constants.BACKSPACE_0:
                case Constants.BACKSPACE_1:
                    for (KeyEventListener l : keyEventListeners)
                        l.onBackspace();
                    if (inputBuffer.length() > 0)
                        inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                    break;

                default:
                    if (input > 0x1F && currentBufferSize() < maxBufferSize) {  // printable characters
                        for (KeyEventListener l : keyEventListeners)
                            l.onPrintableKey((char) input);
                        inputBuffer.append(((char) input));
                    }
                    break;
            }
        }
    }

    /**
     * Handles escaped inputs
     *
     * @param input the raw user input
     */
    private void handleEscapeInputs(int input) {
        escape = false; // fail safe
        switch (input) {
            case Constants.BRACKET: // the next character determines the input
                escape = true;
                break;
            case Constants.ARROW_UP:
                for (KeyEventListener l : keyEventListeners)
                    l.onArrowUp();
                break;
            case Constants.ARROW_DOWN:
                for (KeyEventListener l : keyEventListeners)
                    l.onArrowDown();
                break;
            case Constants.ARROW_RIGHT:
                for (KeyEventListener l : keyEventListeners)
                    l.onArrowRight();
                break;
            case Constants.ARROW_LEFT:
                for (KeyEventListener l : keyEventListeners)
                    l.onArrowLeft();
                break;
            case Constants.EXIT:
                Console.close();
                break;
            default:
                break;
        }
    }

    /**
     * Adds a new key event listener
     * <br>
     * If the listener already was in the listeners list, nothing happens
     *
     * @param listener the listener to add
     */
    public void addKeyEventListener(KeyEventListener listener) {
        if (!keyEventListeners.contains(listener))
            this.keyEventListeners.add(listener);
    }

    /**
     * Removes a key event listener
     * <br>
     * If the listener was not in the listeners list, nothing happens
     *
     * @param listener the listener to remove
     */
    public void removeKeyEventListener(KeyEventListener listener) {
        if (listener != null && keyEventListeners.contains(listener))
            this.keyEventListeners.remove(listener);
    }

    /**
     * Provides the current size of the input string stored in the buffer
     *
     * @return the input length
     */
    protected int currentBufferSize() {
        return inputBuffer.length();
    }

    /**
     * Determines if the console input is enabled
     * <p>
     * ling or disabling the console input does not affect the {@linkplain #listenForRawInput()} method in any way;
     * <ul>
     * <li>Input enabled: the console works as usual, registering all keystrokes and adding new characters in
     * the buffer</li>
     * <li>Input disabled: no echo is provided, "locking" the cursor in place; the console just <i>ignores</i>
     * the keystrokes, not filling the buffer.</li>
     * </ul>
     * Escape sequences are always evaluated.
     *
     * @return true if the input is enabled, false otherwise
     */
    public boolean isConsoleInputEnabled() {
        return inputEnable;
    }

    /**
     * Enables the console input
     *
     * @see #isConsoleInputEnabled()
     */
    public void enableConsoleInput() {
        inputEnable = true;
    }

    /**
     * Disables the console input
     *
     * @see #isConsoleInputEnabled()
     */
    public void disableConsoleInput() {
        inputEnable = false;
    }
}
