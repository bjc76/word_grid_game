package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class GameEventBroadcaster {
    private static final Map<UI, Consumer<String>> players = new ConcurrentHashMap<>();
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static synchronized Registration register(UI ui, Consumer<String> listener) {
        players.put(ui, listener);
        return () -> players.remove(ui);
    }

    public static synchronized void sendGameUpdate(String msg, UI sender) {
        System.out.println("Send update: "+msg);
        players.forEach((ui, listener) -> {
            if (ui != sender) {
                executor.execute(() -> listener.accept(msg));
            }
        });
    }


}
