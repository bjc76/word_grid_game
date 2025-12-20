package uk.ac.cam.bjc76.boggle.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Serialiser {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String serialise(GameEvent o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    public static GameEvent deserialise(String jsonString) throws JsonProcessingException {
        return mapper.readValue(jsonString, GameEvent.class);
    }

}
