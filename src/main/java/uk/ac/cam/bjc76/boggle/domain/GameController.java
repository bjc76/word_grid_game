package uk.ac.cam.bjc76.boggle.domain;

import java.io.IOException;
import java.io.SyncFailedException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GameController {
    private LocalTime endTime;
    private final PlayerGame player1;
    private final PlayerGame player2;
    private final Player otherPlayer;
    private final Player thisPlayer;
    private final WordGrid grid;

    public GameController(int dimensions, ArrayList<String> gridList, LocalTime endTime) throws IOException, SQLException {
        thisPlayer = new Player("#FF0000");
        otherPlayer = new Player("#0000FF");
        ArrayList<Letter> tempLetters = new ArrayList<>();
        for (String s : gridList) {
            tempLetters.add(new Letter(s));
        }
        System.out.println("Length: "+gridList.size());
        grid = new WordGrid(dimensions, tempLetters);
        player1 = new PlayerGame(thisPlayer);
        player2 = new PlayerGame(otherPlayer);
        this.endTime = endTime;
    }


    public ArrayList<Letter> getLettersFromGrid() {
        return grid.getLetters();
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public int getGridDimensions() {
        return grid.getDimension();
    }

    public ArrayList<String> wordSubmitted() throws SQLException, IOException {
        ArrayList<Letter> selectedLetters = grid.getSelectedLetters();
        selectedLetters.sort(Comparator.comparing(Letter::getSelectedTime));
        try {
            if (grid.checkLettersAreJoined(selectedLetters)) {
                if (!player1.checkIfWordAlreadyUsed(selectedLetters) && !player2.checkIfWordAlreadyUsed(selectedLetters)) {
                    if (player1.goAttempt(selectedLetters)) { //checks cells are available and word exists, else throws IOException.
                        ArrayList<String> indexList = new ArrayList<>();
                        for (Letter l : selectedLetters) {
                            indexList.add(String.valueOf(grid.indexLetter(l)));
                        }
                        return indexList;
                    }
                } else {
                    throw new IOException("Word already submitted");
                }
            }
            throw new IOException("Invalid word input (Letters must be connected)");
        } finally { //always deselect letters after attempt at entering word.
            for (Letter l : selectedLetters) {
                l.deSelect();
            }
        }
    }

    public String handleUpdate(String msg) throws SyncFailedException {
        // word processing already done by opponent, so just have to store word in appropriate places.
        String[] msgParts = msg.split("\\|");
        ArrayList<Letter> usedLetters = new ArrayList<>();
        ArrayList<String> receivedIndexes = new ArrayList<>(Arrays.asList(msgParts[1].split(" ")));
        receivedIndexes.removeFirst();
        for (String s : receivedIndexes) {
            usedLetters.add(grid.getLetter(Integer.parseInt(s)));
        }
        player2.insertUsedWord(usedLetters);
        if (otherPlayer.getScore() != Integer.parseInt(msgParts[2])) {
            throw new SyncFailedException("Game out of sync with other player");
        }
        StringBuilder word = new StringBuilder();
        for (Letter l : usedLetters) {
            word.append(l.getValue());
        }
        player1.checkDecayingWords();
        player2.checkDecayingWords();
        return String.valueOf(word);
    }

    public int getScore() {
        return thisPlayer.getScore();
    }

    public int getOtherPlayerScore() {
        return otherPlayer.getScore();
    }

    public void checkDecay() {
        player1.checkDecayingWords();
        player2.checkDecayingWords();
    }

    public String getTimeRemaining() {
        long difference = ChronoUnit.SECONDS.between(LocalTime.now(), endTime);
        return String.valueOf(difference);
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean gameIsFinished() {
        return endTime.isBefore(LocalTime.now());
    }

    public ArrayList<String> getWords() {
        ArrayList<String> words = new ArrayList<>();
        for (WordCombination w : player1.getBank().getCombinations()) {
            words.add(w.getWord());
        }
        return words;
    }

    public ArrayList<String> getOpponentWords() {
        ArrayList<String> words = new ArrayList<>();
        for (WordCombination w : player2.getBank().getCombinations()) {
            words.add(w.getWord());
        }
        return words;
    }

    public void setEndTime(String endTimeReceived) {
        endTime = LocalTime.parse(endTimeReceived);
    }

    public String getMostRecentWord(){
        return player1.getBank().getMostRecentWord();
    }
}
