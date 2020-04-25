package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Block;

import java.util.List;

public interface BuildingBlocksListener {
    void onBlocksObtained(String name, List<Block> blocks);
}
