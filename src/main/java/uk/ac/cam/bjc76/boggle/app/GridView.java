package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
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
    private Span scoreDisplay;
    private Span opponentScoreDisplay;
    private Span timeDisplay;
    private boolean gameOver = false;
    private final TextField nameInput = new TextField("Enter name: ");
    private VerticalLayout myWordsLayout;
    private VerticalLayout opponentWordsLayout;
    private Dialog waitingDialogBox;

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
            buildLayout(isMobile);
        }));
    }


    private void buildLayout(boolean isMobile) {
        removeAll();
        buttonList.clear();

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);

        scoreDisplay = new Span();
        opponentScoreDisplay = new Span();
        timeDisplay = new Span(gameController.getTimeRemaining());
        timeDisplay.getStyle().set("font-weight", "bold");

        // alternate displays for mobile and desktop as full content doesn't fit on mobile.
        if (isMobile) {
            headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            headerLayout.setPadding(false);

            HorizontalLayout scoreGroup = new HorizontalLayout();
            scoreGroup.setSpacing(false);
            scoreGroup.setAlignItems(Alignment.CENTER);

            scoreDisplay.setText("0");
            scoreDisplay.getStyle().set("color", "green").set("font-weight", "bold").set("font-size", "1.5rem");

            Span sep = new Span(" - ");
            sep.getStyle().set("font-weight", "bold").set("font-size", "1.5rem").set("margin", "0 10px");

            opponentScoreDisplay.setText("0");
            opponentScoreDisplay.getStyle().set("color", "red").set("font-weight", "bold").set("font-size", "1.5rem");

            scoreGroup.add(scoreDisplay, sep, opponentScoreDisplay);
            timeDisplay.getStyle().set("font-size", "1.2rem");

            headerLayout.add(scoreGroup, timeDisplay);

            myWordsLayout = null;
            opponentWordsLayout = null;
        } else {
            headerLayout.setJustifyContentMode(JustifyContentMode.AROUND);
            scoreDisplay.setText("Your Score: 0");
            opponentScoreDisplay.setText("Opponent Score: 0");
            Styles.scoreStyle(scoreDisplay);
            Styles.scoreStyle(opponentScoreDisplay);
            Styles.scoreStyle(timeDisplay);
            headerLayout.add(scoreDisplay, timeDisplay, opponentScoreDisplay);

            myWordsLayout = new VerticalLayout(new Span("My Words:"));
            myWordsLayout.setWidth("200px");
            opponentWordsLayout = new VerticalLayout(new Span("Opponent's Words:"));
            opponentWordsLayout.setWidth("200px");
        }

        Div boardContainer = new Div();
        boardContainer.getStyle().set("display", "grid");
        boardContainer.getStyle().set("grid-template-columns", "repeat(" + gameController.getGridDimensions() + ", 1fr)");
        boardContainer.getStyle().set("gap", "4px");
        boardContainer.getStyle().set("width", "100%"); // Fill the gameContainer
        boardContainer.getStyle().set("box-sizing", "border-box"); // Ensure padding doesn't cause overflow

        for (Letter l : gameController.getLettersFromGrid()) {
            CellButton cellBtn = new CellButton(new GridCell(l, gameController.getThisPlayer()));
            Button btn = cellBtn.getButton();

            btn.setWidth("100%");
            btn.setHeight("auto");
            btn.getStyle().set("aspect-ratio", "1 / 1");
            btn.getStyle().set("padding", "0");

            btn.getStyle().set("min-width", "0");
            btn.getStyle().set("font-size", isMobile ? "1.2em" : "1.5em");
            btn.getStyle().set("margin", "0");

            boardContainer.add(btn);
            buttonList.add(cellBtn);
        }

        Button submitWordButton = new Button("Submit");
        submitWordButton.addClickListener(e -> handleSubmit());
        submitWordButton.addThemeName("primary");
        submitWordButton.getStyle().set("margin-top", "15px");

        VerticalLayout gameContainer = new VerticalLayout();
        gameContainer.setPadding(false);
        gameContainer.setSpacing(false);
        gameContainer.setAlignItems(Alignment.CENTER);
        gameContainer.add(boardContainer, submitWordButton);

        if (isMobile) {
            gameContainer.setWidth("95%");
            gameContainer.setMaxWidth("400px");
            submitWordButton.setWidth("50%");
        } else {
            gameContainer.setWidth("600px");
            submitWordButton.setWidth("200px");
        }

        if (isMobile) {
            Div spacer = new Div();
            spacer.setHeight("40px");
            spacer.setWidthFull();

            setSizeFull();
            setPadding(true);
            setJustifyContentMode(JustifyContentMode.START);
            setAlignItems(Alignment.CENTER);
            add(spacer, headerLayout, gameContainer);
        } else {
            HorizontalLayout mainContainer = new HorizontalLayout();
            mainContainer.setWidthFull();
            mainContainer.setJustifyContentMode(JustifyContentMode.CENTER);

            VerticalLayout centerLayout = new VerticalLayout();
            centerLayout.setWidth("auto");
            centerLayout.setAlignItems(Alignment.CENTER);
            centerLayout.add(gameContainer);

            mainContainer.add(myWordsLayout, centerLayout, opponentWordsLayout);
            add(headerLayout, mainContainer);
        }

        gameOver = false;
    }

    private void handleSubmit() {
        try {
            ArrayList<String> validWordIndex = gameController.wordSubmitted();

            if (myWordsLayout != null) {
                String word = gameController.getMostRecentWord();
                Span wordSpan = new Span(word);
                wordSpan.getStyle().set("color", "green");
                myWordsLayout.getElement().insertChild(1, wordSpan.getElement());
                scoreDisplay.setText("Your Score: " + gameController.getScore());
            } else {
                String word = gameController.getMostRecentWord();
                Notification.show("Found: " + word, 1000, Notification.Position.BOTTOM_CENTER);
                scoreDisplay.setText(String.valueOf(gameController.getScore()));
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

            if (opponentWordsLayout != null) {
                Notification.show("Other player submitted word: " + word);
                Span wordSpan = new Span(word);
                wordSpan.getStyle().set("color", "green");
                opponentWordsLayout.getElement().insertChild(1, wordSpan.getElement());
                opponentScoreDisplay.setText("Opponent Score: " + gameController.getOtherPlayerScore());
            } else {
                Notification.show("Opponent found: " + word, 1000, Notification.Position.BOTTOM_CENTER);
                opponentScoreDisplay.setText(String.valueOf(gameController.getOtherPlayerScore()));
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
            timeDisplay.setText("Time remaining: "+gameController.getTimeRemaining());
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
