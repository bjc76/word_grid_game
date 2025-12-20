package uk.ac.cam.bjc76.boggle.domain;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
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

    public void input(ArrayList<Letter> letters) {

    }

    public void handleUpdate(String msg) {

    }

}
