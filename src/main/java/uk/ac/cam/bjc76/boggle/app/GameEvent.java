package uk.ac.cam.bjc76.boggle.app;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StartGameUpdate.class, name = "start"),
        @JsonSubTypes.Type(value = GameEvent.class, name = "update")
})



public abstract class GameEvent {
    private String senderID;

    public String convertToJson() {
        return null;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderID() {
        return senderID;
    }
}
