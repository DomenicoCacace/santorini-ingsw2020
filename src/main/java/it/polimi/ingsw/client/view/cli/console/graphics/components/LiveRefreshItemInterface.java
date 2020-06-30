package it.polimi.ingsw.client.view.cli.console.graphics.components;

import java.util.List;

/**
 * Interface for graphical items which need to be refreshed based on the current item selected
 */
public interface LiveRefreshItemInterface {

    /**
     * Refreshes the details to show
     *
     * @param info the details
     */
    void refresh(List<String> info);
}
