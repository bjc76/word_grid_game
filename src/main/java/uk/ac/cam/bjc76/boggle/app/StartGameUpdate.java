package uk.ac.cam.bjc76.boggle.app;

import java.time.LocalTime;

public class StartGameUpdate extends GameEvent{
    String type = "start";
    String startTime;
    String sessionKey;

    public StartGameUpdate() {}

    public StartGameUpdate(LocalTime startTime, String sessionKey) {
        this.startTime = String.valueOf(startTime);
        this.sessionKey = sessionKey;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
