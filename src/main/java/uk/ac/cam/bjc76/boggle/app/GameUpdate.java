package uk.ac.cam.bjc76.boggle.app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalTime;
import java.util.ArrayList;

public class GameUpdate extends GameEvent{
    String type = "update";
    String timeOfUpdate;
    String[] letterCoordinates;

    public GameUpdate() {}

    public GameUpdate(LocalTime timeOfUpdate, ArrayList<String> letterCoordinatesArr) {
        this.timeOfUpdate = String.valueOf(timeOfUpdate);

        this.letterCoordinates = new String[letterCoordinatesArr.size()];
        this.letterCoordinates = letterCoordinatesArr.toArray(this.letterCoordinates);
    }

    public void setTimeOfUpdate(String timeOfUpdate) {
        this.timeOfUpdate = timeOfUpdate;
    }

    public String getTimeOfUpdate() {
        return timeOfUpdate;
    }

    public void setLetterCoordinates(String[] letterCoordinates) {
        this.letterCoordinates = letterCoordinates;
    }

    public String[] getLetterCoordinates() {
        return letterCoordinates;
    }
}
