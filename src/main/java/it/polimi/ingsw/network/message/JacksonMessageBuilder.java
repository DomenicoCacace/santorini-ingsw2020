package it.polimi.ingsw.network.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.message.request.MessageRequest;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.IOException;

//Used to parse from message to string and vice versa
public class JacksonMessageBuilder {
    private ObjectMapper objectMapper;

    public JacksonMessageBuilder() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public MessageRequest fromStringToRequest(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, MessageRequest.class);
    }

    public String fromRequestToString(MessageRequest message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }

    public MessageResponse fromStringToResponse(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, MessageResponse.class);
    }

    public String fromResponseToString(MessageResponse message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
