package it.polimi.ingsw.network.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

//Used to parse from message to string and vice versa
public class JacksonMessageBuilder {
    private final ObjectMapper objectMapper;

    public JacksonMessageBuilder() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String fromMessageToString(Message message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            System.out.println("Cannot serialize message");
            return ""; //TODO: this shouldn't happen
        }
    }

    public Message fromStringToMessage(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Message.class);
        } catch (IOException e) {
            System.out.println("Cannot deserialize string");
            return null; //TODO this shouldn't happen (but it will)
        }
    }
}
