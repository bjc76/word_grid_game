package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Route("")
public class GridView extends VerticalLayout {
    private int DIMENSIONS = 6;
    private int DURATION = 1;
    private final GameFactory gameFactory = new GameFactory();
    private ArrayList<CellButton> buttonList = new ArrayList<>();
    private Registration broadcasterRegistration;
    private GameController gameController;
    private Span scoreDisplay;
    private Span opponentScoreDisplay;
    private Span timeDisplay;
    private boolean gameOver = false;
    private TextField nameInput = new TextField("Enter name: ");
    private VerticalLayout myWordsLayout;
    private VerticalLayout opponentWordsLayout;
    private Dialog waitingDialogBox;
    private LocalTime lastConnectionTime = LocalTime.now();

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
        String endTime = msg.split("\\|")[1];
        gameController.setEndTime(endTime);
        waitingDialogBox.close();
        removeAll();
        createGrid();
    }

    private void joinGame(String msg) {
        String opponentName = msg.split("\\|")[3];
        DialogBoxes.showGameRequestDialog(opponentName,
                () -> {
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
                () -> {
            getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate("C|confirm", ui));
            initialisePage("");
        }
        );
    }

    public void createGrid() {
        if (gameController == null || gameController.getLettersFromGrid() == null) return;

        HorizontalLayout mainContainer = new HorizontalLayout();
        mainContainer.setWidthFull();
        mainContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        mainContainer.setAlignItems(Alignment.START);

        myWordsLayout = new VerticalLayout();
        myWordsLayout.setWidth("200px");
        myWordsLayout.add(new Span("My Words:"));

        opponentWordsLayout = new VerticalLayout();
        opponentWordsLayout.setWidth("200px");
        opponentWordsLayout.add(new Span("Opponent's Words:"));

        VerticalLayout centerLayout = new VerticalLayout();
        centerLayout.setWidth("auto");
        centerLayout.setAlignItems(Alignment.CENTER); // Centers everything vertically

        VerticalLayout boardLayout = new VerticalLayout();
        boardLayout.setAlignItems(Alignment.CENTER); // Centers the rows
        boardLayout.setPadding(false);

        HorizontalLayout currentRow = new HorizontalLayout();
        currentRow.setJustifyContentMode(JustifyContentMode.CENTER); // Centers buttons in row

        buttonList = new ArrayList<>();
        int count = 0;
        for (Letter l : gameController.getLettersFromGrid()) {
            CellButton button = new CellButton(new GridCell(l, gameController.getThisPlayer()));
            buttonList.add(button);
            currentRow.add(button.getButton());
            if (++count % gameController.getGridDimensions() == 0) {
                boardLayout.add(currentRow);
                currentRow = new HorizontalLayout();
                currentRow.setJustifyContentMode(JustifyContentMode.CENTER);
            }
        }

        scoreDisplay = new Span("Your Score: 0");
        opponentScoreDisplay = new Span("Opponent Score: 0");
        timeDisplay = new Span("Time remaining: " + gameController.getTimeRemaining());

        Styles.scoreStyle(scoreDisplay);
        Styles.scoreStyle(opponentScoreDisplay);
        Styles.scoreStyle(timeDisplay);

        Button submitWordButton = new Button("Submit");
        submitWordButton.addClickListener(e -> {
            try {
                ArrayList<String> validWordIndex = gameController.wordSubmitted();
                scoreDisplay.setText("Your Score: " + gameController.getScore());

                StringBuilder indexString = new StringBuilder("U|");
                for (String s : validWordIndex) {
                    indexString.append(" ").append(s);
                }
                indexString.append("|");
                indexString.append(gameController.getScore());
                getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate(String.valueOf(indexString), ui));

                String word = gameController.getMostRecentWord();
                Span wordSpan = new Span(word);
                wordSpan.getStyle().set("color", "green");
                myWordsLayout.getElement().insertChild(1, wordSpan.getElement());


            } catch (IOException ex) {
                Notification.show(ex.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
                Notification.show("SQL error: " + ex.getMessage());
            }

            for (CellButton c : buttonList) {
                c.deselect();
            }
        });

        gameOver = false;
        centerLayout.add(scoreDisplay, opponentScoreDisplay, timeDisplay, boardLayout, submitWordButton);
        mainContainer.add(myWordsLayout, centerLayout, opponentWordsLayout);
        add(mainContainer);
    }


    private void updateGameFromOpponent(String msg) {
        try {
            String word = gameController.handleUpdate(msg);
            Notification.show("Other player submitted word: " + word);
            Span wordSpan = new Span(word);
            wordSpan.getStyle().set("color", "green");
            opponentWordsLayout.getElement().insertChild(1,wordSpan.getElement());
            opponentScoreDisplay.setText("Opponent Score: "+String.valueOf(gameController.getOtherPlayerScore()));
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
        } else {
            boolean userWon = scoreReceived < gameController.getScore();
            DialogBoxes.showGameOverDialog(userWon, gameController.getScore(), scoreReceived, this::resetGame);
        }
    }


    private void checkGameOver() {
        if (gameController.gameIsFinished()) {
            gameOver = true;
            Notification.show("Realised game over, sent message");
            getUI().ifPresent(ui -> GameEventBroadcaster.sendGameUpdate("F|"+gameController.getScore(), ui));
        } else {
            timeDisplay.setText("Time remaining: "+gameController.getTimeRemaining());
        }
    }

    private void checkDisconnection() {
        if (ChronoUnit.SECONDS.between(LocalTime.now(), lastConnectionTime) > 2) {
            DialogBoxes.showDisconnectedDialog(() -> initialisePage(nameInput.getValue()));
            gameOver = true;
        }
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = GameEventBroadcaster.register(ui, message -> {
            ui.access(() -> {
//                Notification.show("Update received!");
                lastConnectionTime = LocalTime.now();
                try {
                    if ("S".equals(message.split("\\|")[0])) {
                        joinGame(message);
                    } else if ("U".equals(message.split("\\|")[0])) {
                        updateGameFromOpponent(message);
                    } else if ("F".equals(message.split("\\|")[0])) {
                        Notification.show("Update received!");
                        gameFinished(message);
                    } else if ("C".equals(message.split("\\|")[0])) {
                        confirmStartGame(message);
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
                checkDisconnection();
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
