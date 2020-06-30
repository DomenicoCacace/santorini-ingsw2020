package it.polimi.ingsw.server.listeners;

import it.polimi.ingsw.shared.dataClasses.Block;

import java.util.List;

/**
 * Listens for a build action which offers the possibility to choose which building block to build
 */
public interface BuildingBlocksListener {

    /**
     * Notifies that a build action offers the player to choose what block to build
     *
     * @param name   the player to notify's username
     * @param blocks the
     */
    void onBlocksObtained(String name, List<Block> blocks);
}
