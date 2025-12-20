package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import uk.ac.cam.bjc76.boggle.domain.GameController;
import uk.ac.cam.bjc76.boggle.domain.GameFactory;
import uk.ac.cam.bjc76.boggle.domain.GridCell;
import uk.ac.cam.bjc76.boggle.domain.Letter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;


public class GridView extends VerticalLayout {
    private GameFactory gameFactory = new GameFactory();
    private ArrayList<CellButton> buttonList = new ArrayList<>();
    private Registration broadcasterRegistration;
    private GameController gameController;

    public GridView(GameController gameController) {
        Button startButton = new Button("Start game");
        startButton.addClickListener(e -> {
            startGame();
        });
        add(startButton);

    }

    private void startGame() {
        try {
            gameController = gameFactory.startNewGame();
        } catch (IOException | SQLException e) {
            return;
        }

        StringBuilder msg = new StringBuilder("S");
        for (Letter l : gameController.getLettersFromGrid()) {
            msg.append(l.getValue());
        }
        msg.append(LocalTime.now());
        getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(String.valueOf(msg), ui));
        removeAll();
        createGrid();
    }

    private void joinGame(String msg) {
        try {
            gameController = gameFactory.startRecievedGame(msg);
        } catch (IOException | SQLException e) {
            return;
        }
        removeAll();
        createGrid();
    }

    public void createGrid() {
        VerticalLayout boardLayout = new VerticalLayout();
        HorizontalLayout currentRow = new HorizontalLayout();
        int count = 0;
        for (Letter l : gameController.getLettersFromGrid()){
            CellButton button = new CellButton(new GridCell(l, gameController.getThisPlayer()));
            buttonList.add(button);
            currentRow.add(button.getButton());
            if (++count % gameController.getGridDimensions() == 0) {
                boardLayout.add(currentRow);
                currentRow = new HorizontalLayout();
            }
        }
        add(boardLayout);
    }

    private void updateGameFromOpponent(String msg) {

    }

    private void gameFinished(String msg) {

    }

    private void gameFinished() {

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = GameEventBroadcaster.register(ui, message -> {
            ui.access(() -> {
                if ('S' == message.charAt(0)) {
                    joinGame(message);
                } else if ('U' == message.charAt(0)) {
                    updateGameFromOpponent(message);
                } else if ('F' == message.charAt(0)) {
                    gameFinished(message);
                }
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

}
