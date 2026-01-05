package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import uk.ac.cam.bjc76.boggle.domain.GameController;
import uk.ac.cam.bjc76.boggle.domain.GameFactory;
import uk.ac.cam.bjc76.boggle.domain.Letter;

import java.io.IOException;
import java.io.SyncFailedException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

@Route("")
public class GridView extends VerticalLayout {
    private int DIMENSIONS = 6;
    private int DURATION = 2;
    private final GameFactory gameFactory = new GameFactory();
    private final ArrayList<CellButton> buttonList = new ArrayList<>();
    private Registration broadcasterRegistration;
    private GameController gameController;
    private boolean gameOver = false;
    private final TextField nameInput = new TextField("Enter name: ");
    private Dialog waitingDialogBox;
    private ScreenLayout screenLayout;

    public GridView() {
        initialisePage("");
    }

    public void initialisePage(String name) {
        removeAll();
        setSizeFull();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        Button startButton = new Button("Start game");
        startButton.addClickListener(e -> {
            attemptStartGame();
        });
        nameInput.setValue(name);
        add(nameInput);
        add(startButton);
    }

    // Sends a message with grid data to other users to initiate game
    private void attemptStartGame() {
        try {
            gameController = gameFactory.startNewGame(DIMENSIONS, DURATION);
        } catch (IOException | SQLException e) {
            return;
        }
        StringBuilder msg = new StringBuilder("S|");
        for (Letter l : gameController.getLettersFromGrid()) {
            msg.append(" ").append(l.getValue());
        }
        msg.append("|");
        msg.append(DIMENSIONS);
        msg.append("|");
        msg.append(nameInput.getValue());
        getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(String.valueOf(msg), ui));
        waitingDialogBox = DialogBoxes.showWaitingDialog(() -> initialisePage(nameInput.getValue()));
    }

    private void confirmStartGame(String msg) {
        if (waitingDialogBox != null){
            String endTime = msg.split("\\|")[1];
            if (Objects.equals(endTime, "decline")) {
                initialisePage(nameInput.getValue());
            } else {
                gameController.setEndTime(endTime);
                waitingDialogBox.close();
                waitingDialogBox = null;
                removeAll();
                createGrid();
            }
        }
    }

    private void joinGame(String msg) {
        String opponentName = msg.split("\\|")[3];
        DialogBoxes.showGameRequestDialog(opponentName,
                () -> { // method if game accepted.
            try {
                gameController = gameFactory.startReceivedGame(msg, DURATION);
                getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(
                        "C|"+LocalTime.now().plusMinutes(DURATION), ui));
                removeAll();
                createGrid();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                Notification.show("Error joining game: "+e.getMessage());
                initialisePage("");
            }
        },
                () -> { // action on decline.
            getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate("C|decline", ui));
            initialisePage("");
        }
        );
    }


    public void createGrid() {
        if (gameController == null) return;

        getUI().ifPresent(ui -> ui.getPage().retrieveExtendedClientDetails(details -> {
            int screenWidth = details.getBodyClientWidth();
            boolean isMobile = screenWidth < 600;
            removeAll();
            buttonList.clear();
            screenLayout = new ScreenLayout(isMobile, gameController, buttonList, this::handleSubmit);
            gameOver=false;
        }));
    }

    public void handleSubmit() {
        try {
            ArrayList<String> validWordIndex = gameController.wordSubmitted();

            if (screenLayout.getMyWordsLayout() != null) {
                String word = gameController.getMostRecentWord();
                Span wordSpan = new Span(word);
                wordSpan.getStyle().set("color", "green");
                screenLayout.getMyWordsLayout().getElement().insertChild(1, wordSpan.getElement());
                screenLayout.getScoreDisplay().setText("Your Score: " + gameController.getScore());
            } else {
                String word = gameController.getMostRecentWord();
                Notification.show("Found: " + word, 1000, Notification.Position.BOTTOM_CENTER);
                screenLayout.getScoreDisplay().setText(String.valueOf(gameController.getScore()));
            }

            StringBuilder indexString = new StringBuilder("U|");
            for (String s : validWordIndex) {
                indexString.append(" ").append(s);
            }
            indexString.append("|").append(gameController.getScore());
            getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(String.valueOf(indexString), ui));

        } catch (IOException ex) {
            Notification.show(ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            Notification.show("SQL error: " + ex.getMessage());
        }

        for (CellButton c : buttonList) {
            c.deselect();
        }
    }

    private void updateGameFromOpponent(String msg) {
        try {
            String word = gameController.handleUpdate(msg);

            if (screenLayout.getOpponentWordsLayout() != null) {
                Notification.show("Other player submitted word: " + word);
                Span wordSpan = new Span(word);
                wordSpan.getStyle().set("color", "green");
                screenLayout.getOpponentWordsLayout().getElement().insertChild(1, wordSpan.getElement());
                screenLayout.getOpponentScoreDisplay().setText("Opponent Score: " + gameController.getOtherPlayerScore());
            } else {
                Notification.show("Opponent found: " + word, 1000, Notification.Position.BOTTOM_CENTER);
                screenLayout.getOpponentScoreDisplay().setText(String.valueOf(gameController.getOtherPlayerScore()));
            }

            for (CellButton button : buttonList) {
                button.updateButtonColour();
            }
        } catch (SyncFailedException e) {
            throw new RuntimeException(e);
        }
    }


    private void resetGame(){
        Notification.show("Game closed, please reconnect to replay");
        initialisePage(nameInput.getValue());
    }

    private void gameFinished(String msg) {
        if (!gameOver) {
            checkGameOver();
        }
        gameOver = true;
        int scoreReceived = Integer.parseInt(msg.split("\\|")[1]);
        if (scoreReceived != gameController.getOtherPlayerScore()) {
            Notification.show("Sync error, please refresh page");
            gameController = null;
        } else {
            boolean userWon = scoreReceived < gameController.getScore();
            DialogBoxes.showGameOverDialog(userWon, gameController.getScore(), scoreReceived, this::resetGame);
            gameController = null;
        }
    }


    private void checkGameOver() {
        if (gameController.gameIsFinished()) {
            gameOver = true;
            Notification.show("Realised game over, sent message");
            getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate("F|"+gameController.getScore(), ui));
        } else {
            screenLayout.getTimeDisplay().setText("Time remaining: "+gameController.getTimeRemaining());
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = GameEventBroadcaster.register(ui, message -> {
            ui.access(() -> {
//                Notification.show("Update received!");
                try {
                    String type = message.split("\\|")[0];
                    if ("S".equals(type)) {
                        if (gameController == null && waitingDialogBox == null) {
                            joinGame(message);
                        }
                    } else if ("U".equals(type)) {
                        if (gameController != null) {
                            updateGameFromOpponent(message);
                        }
                    } else if ("F".equals(type)) {
                        if (gameController != null) {
                            gameFinished(message);
                        }
                    } else if ("C".equals(type)) {
                        if (waitingDialogBox != null) {
                            confirmStartGame(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show("Error processing msg: " + e.getMessage());
                }
            });
        });

        ui.setPollInterval(200);
        ui.addPollListener(e -> {
            if (gameController != null){
                gameController.checkDecay();
                for (CellButton button : buttonList) {
                    button.updateButtonColour();
                }
                if (!gameOver) {
                    checkGameOver();
                }
            }
        });


    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        detachEvent.getUI().setPollInterval(-1);
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

}
