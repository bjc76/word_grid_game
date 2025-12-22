package uk.ac.cam.bjc76.boggle.domain;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class GameFactory {
    public GameController startReceivedGame(String msg, int duration) throws SQLException, IOException {
        String[] msgSections = msg.split("\\|");
        ArrayList<String> lettersAsStrings = new ArrayList<>(Arrays.asList(msgSections[1].split(" ")));
        lettersAsStrings.removeFirst();
        int dimensions = Integer.parseInt(msgSections[2]);
        return new GameController(
                dimensions,
                lettersAsStrings,
                LocalTime.now().plusMinutes(duration)
        );
    }

    public GameController startNewGame(int dimensions, int minutes) throws SQLException, IOException {
        return new GameController(
                dimensions,
                LetterGenerator.generate(dimensions * dimensions),
                LocalTime.now().plusMinutes(minutes)
        );
    }
}
