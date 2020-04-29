package it.polimi.ingsw.network.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;

import java.io.IOException;

//Used to parse from message to string and vice versa
public class JacksonMessageBuilder {
    private final ObjectMapper objectMapper;
    private final ObjectReader objectReader;

    public JacksonMessageBuilder() {
        objectMapper = new ObjectMapper();
        objectReader = objectMapper.readerFor(Message.class);
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

    public Message fromStringToMessage(String jsonString) throws IOException {
        return objectReader.readValue(jsonString); //TODO: may return null when the client interrupts
    }
}
