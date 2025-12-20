package uk.ac.cam.bjc76.boggle.domain;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class GameFactory {
    public GameController startRecievedGame(String msg) throws SQLException, IOException {
        ArrayList<String> lettersAsStrings = new ArrayList<>();
        for (int i = 1; i < msg.length()-7; i++) {
            lettersAsStrings.add(String.valueOf(msg.charAt(i)));
        }
        LocalTime endTime = LocalTime.parse(msg.substring(msg.length()-7));
        return new GameController(
                "ben",
                "bob",
                5,
                lettersAsStrings,
                endTime
        );
    }

    public GameController startNewGame() throws SQLException, IOException {
        return new GameController(
                "ben",
                "bob",
                25,
                LetterGenerator.generate(25),
                LocalTime.now()
        );
    }
}
