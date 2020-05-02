package it.polimi.ingsw.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects.BuildBeforeAfterMovement;
import it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects.CannotMoveUp;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainDifferentCell;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainSameCell;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.MoveAgain;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Swap;
import it.polimi.ingsw.model.godCardsEffects.winConditionEffects.Down2Levels;
import it.polimi.ingsw.model.rules.RuleSetBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class godGen {

    @Test
    void godGenTest() throws IOException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Base", 2, "No Effect"));
        gods.get(0).setStrategy(new RuleSetBase());
        gods.add(new God("Apollo", 2, ""));
        gods.get(1).setStrategy(new Swap());
        gods.add(new God("Minotaur", 2, ""));
        gods.get(2).setStrategy(new Push());
        gods.add(new God("Artemis", 2, ""));
        gods.get(3).setStrategy(new MoveAgain());
        gods.add(new God("Atlas", 2, ""));
        gods.get(4).setStrategy(new BuildDome());
        gods.add(new God("Demeter", 2, ""));
        gods.get(5).setStrategy(new BuildAgainDifferentCell());
        gods.add(new God("Hephaestus", 2, ""));
        gods.get(6).setStrategy(new BuildAgainSameCell());
        gods.add(new God("Prometheus", 2, ""));
        gods.get(7).setStrategy(new BuildBeforeAfterMovement());
        gods.add(new God("Athena", 2, ""));
        gods.get(8).setStrategy(new CannotMoveUp());
        gods.add(new God("Pan", 2, ""));
        gods.get(9).setStrategy(new Down2Levels());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("C:\\Desktop\\Uni\\Progetto INGSW\\ing-sw-2020-Albanese-Boldini-Cacace\\ProvaAM37\\ProvaAM37\\GodsConfigFile.json"), gods);
        } catch (IOException e) {
            e.printStackTrace();
        }
        objectMapper = new ObjectMapper();
        List<God> deserializedGod = objectMapper.readerFor(new TypeReference<List<God>>(){})
                .readValue(new File ("C:\\Desktop\\Uni\\Progetto INGSW\\ing-sw-2020-Albanese-Boldini-Cacace\\ProvaAM37\\ProvaAM37\\newGods.json"));
        System.out.println("Yolo");
    }
}
