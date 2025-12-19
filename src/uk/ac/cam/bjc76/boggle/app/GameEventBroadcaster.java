package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.UI;


public class GameEventBroadcaster {
    private static UI player1;
    private static UI player2;

    public static synchronized void register(UI ui) {
        if (player1 == null) {
            player1 = ui;
        } else if (player2 == null) {
            player2 = ui;
        } else {
            throw new IllegalStateException("Max 2 players");
        }
    }

    public static synchronized void unregister(UI ui) {
        if (ui == player1) {
            player1 = null;
        } else if (ui == player2) {
            player2 = null;
        }
    }

    public static synchronized void sendGameUpdate(String msg, UI sender) {
        UI recipient = (sender == player1) ? player2 : player1;
        if (recipient != null) {
            recipient.access(
                    () -> {
                        
                    }
            )
        }
    }


}
