package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.message.MessageRequest;
import it.polimi.ingsw.network.message.MessageResponse;
import it.polimi.ingsw.network.server.Lobby;

public class MessageParser {

    private Lobby lobby;
    private ServerController serverController;

    public void parseMessageFromClientToServer(MessageRequest message){
        //chiamerò metodi di lobby e controller
    }

    public void parseMessageFromServerToClient(MessageResponse message){
        //chiamerò i metodi sul network
    }
}
