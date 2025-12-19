package uk.ac.cam.bjc76.boggle.server;

import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game")
public class RelaySocket {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void open(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session sender) {
        sessions.stream()
                .filter(s -> s != sender)
                .forEach(s -> s.getAsyncRemote().sendText(message));
    }
}
