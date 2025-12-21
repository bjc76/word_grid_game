package uk.ac.cam.bjc76.boggle.domain;

import java.io.IOException;
import java.io.SyncFailedException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GameController {
    private LocalTime endTime;
    private PlayerGame player1;
    private PlayerGame player2;
    private Player otherPlayer;
    private Player thisPlayer;
    private WordGrid grid;

    public GameController(String player1name, String player2name, int dimensions, ArrayList<String> gridList, LocalTime endTime) throws IOException, SQLException {
        thisPlayer = new Player(player1name, "#FF0000");
        otherPlayer = new Player(player2name, "#0000FF");
        ArrayList<Letter> tempLetters = new ArrayList<>();
        for (String s : gridList) {
            tempLetters.add(new Letter(s));
        }
        System.out.println("Length: "+gridList.size());
        grid = new WordGrid(dimensions, tempLetters);
        player1 = new PlayerGame(thisPlayer, grid);
        player2 = new PlayerGame(otherPlayer, grid);
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
        if (grid.checkLettersAreJoined(selectedLetters)) {
            if (!player1.checkIfWordAlreadyUsed(selectedLetters) && !player2.checkIfWordAlreadyUsed(selectedLetters)){
                if (player1.goAttempt(selectedLetters)) {
                    ArrayList<String> indexList = new ArrayList<>();
                    for (Letter l : selectedLetters) {
                        l.deSelect();
                        indexList.add(String.valueOf(grid.indexLetter(l)));
                    }
                    return indexList;
                } else {
                    throw new IOException("Invalid word input (Not in dictionary)");
                }
            } else {
                throw new IOException("Word already submitted");
            }
        }
        throw new IOException("Invalid word input (Letters must be connected)");
    }

    public String handleUpdate(String msg) throws SyncFailedException {
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

    public LocalTime getEndTime() {
        return endTime;
    }
}
