package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import uk.ac.cam.bjc76.boggle.domain.GameController;
import uk.ac.cam.bjc76.boggle.domain.GameFactory;
import uk.ac.cam.bjc76.boggle.domain.GridCell;
import uk.ac.cam.bjc76.boggle.domain.Letter;
import uk.ac.cam.bjc76.boggle.ui.Styles;

import java.io.IOException;
import java.io.SyncFailedException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

@Route("")
public class GridView extends VerticalLayout {
    private final GameFactory gameFactory = new GameFactory();
    private ArrayList<CellButton> buttonList = new ArrayList<>();
    private Registration broadcasterRegistration;
    private GameController gameController;
    private Span scoreDisplay;
    private Span opponentScoreDisplay;


    public GridView() {
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
        StringBuilder msg = new StringBuilder("S|");
        for (Letter l : gameController.getLettersFromGrid()) {
            msg.append(" ").append(l.getValue());
        }
        msg.append("|");
        msg.append(gameController.getEndTime());
        getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(String.valueOf(msg), ui));
        removeAll();
        createGrid();
    }

    private void joinGame(String msg) {
        try {
            gameController = gameFactory.startReceivedGame(msg);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Notification.show("Error joining game: "+e.getMessage());
            return;
        }
        removeAll();
        createGrid();
    }

    public void createGrid() {
        VerticalLayout boardLayout = new VerticalLayout();
        HorizontalLayout currentRow = new HorizontalLayout();
        buttonList = new ArrayList<>();
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

        scoreDisplay = new Span("Your Score: 0");
        opponentScoreDisplay = new Span("Opponent Score: 0");
        Styles.scoreStyle(scoreDisplay);
        Styles.scoreStyle(opponentScoreDisplay);

        Button submitWordButton = new Button("Submit");
        submitWordButton.addClickListener(e -> {
            try {
                ArrayList<String> validWordIndex = gameController.wordSubmitted();
                for (CellButton c : buttonList) {
                    c.deselect();
                }
                scoreDisplay.setText("Your Score: " + gameController.getScore());
                StringBuilder indexString = new StringBuilder("U|");
                for (String s : validWordIndex) {
                    indexString.append(" ").append(s);
                }
                indexString.append("|");
                indexString.append(gameController.getScore());
                getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(String.valueOf(indexString), ui));
            } catch (IOException ex) {
                Notification.show(ex.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
                Notification.show("SQL error: " + ex.getMessage());
            }
        });

        add(scoreDisplay);
        add(opponentScoreDisplay);
        add(boardLayout);
        add(submitWordButton);
    }

    private void updateGameFromOpponent(String msg) {
        try {
            String word = gameController.handleUpdate(msg);
            Notification.show("Other player submitted word: " + word);
            opponentScoreDisplay.setText("Opponent Score: "+String.valueOf(gameController.getOtherPlayerScore()));
        } catch (SyncFailedException e) {
            throw new RuntimeException(e);
        }
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
                Notification.show("Update received!");
                try {
                    if ('S' == message.charAt(0)) {
                        joinGame(message);
                    } else if ('U' == message.charAt(0)) {
                        updateGameFromOpponent(message);
                    } else if ('F' == message.charAt(0)) {
                        gameFinished(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show("Error processing msg: " + e.getMessage());
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
