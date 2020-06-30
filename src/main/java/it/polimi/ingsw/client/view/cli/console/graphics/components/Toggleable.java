package it.polimi.ingsw.client.view.cli.console.graphics.components;

import it.polimi.ingsw.client.view.cli.console.KeyEventListener;

/**
 * Interface for items which input can be toggled on or off
 */
public interface Toggleable extends KeyEventListener {

    /**
     * Enables the component
     */
    void enable();

    /**
     * Disables the component
     */
    void onDisable();
}
