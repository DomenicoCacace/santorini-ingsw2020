package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Block;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class PossibleBuildingBlockResponse extends MessageFromServerToClient {

    private final List<Block> blocks;

    @JsonCreator
    public PossibleBuildingBlockResponse(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("blocks") List<Block> blocks) {
        super(username, type);
        this.blocks = blocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onBuildingCellSelected(this);
    }

}
