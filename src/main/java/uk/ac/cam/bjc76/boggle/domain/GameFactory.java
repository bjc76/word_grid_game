package uk.ac.cam.bjc76.boggle.domain;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class GameFactory {
    public GameController startReceivedGame(String msg) throws SQLException, IOException {
        String[] msgSections = msg.split("\\|");
        ArrayList<String> lettersAsStrings = new ArrayList<>(Arrays.asList(msgSections[1].split(" ")));
        lettersAsStrings.removeFirst();
        LocalTime endTime = LocalTime.parse(msgSections[2]);
        return new GameController(
                "ben",
                "bob",
                4,
                lettersAsStrings,
                endTime
        );
    }

    public GameController startNewGame() throws SQLException, IOException {
        return new GameController(
                "bob",
                "ben",
                4,
                LetterGenerator.generate(16),
                LocalTime.now().plusMinutes(4)
        );
    }
}
