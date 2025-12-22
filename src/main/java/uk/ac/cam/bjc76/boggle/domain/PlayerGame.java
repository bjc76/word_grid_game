package uk.ac.cam.bjc76.boggle.domain;

import java.io.IOException;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerGame {
    private final DecayQueue decayingWords = new DecayQueue();
    private final WordBank bank = new WordBank();
    private final Player player;

    public PlayerGame(Player player) throws SQLException {
        this.player = player;
    }

    public WordBank getBank() {
        return bank;
    }

    public boolean goAttempt(ArrayList<Letter> lettersUsed) throws SQLException, IOException {
        StringBuilder word = new StringBuilder();
        for (Letter l : lettersUsed) {
            word.append(l.getValue());
        }
        if (Validator.checkWordIsValid(String.valueOf(word).toLowerCase())) {
            WordCombination newWord = new WordCombination(lettersUsed);
            for (Letter l : newWord.getLetters()) {
                if (!l.validPlayerUsage(player)) {
                    throw new IOException("Cells occupied by opponent");
                }
            }
            newWord.addLetterExpiry();
            for (Letter l : lettersUsed) {
                l.setPlayer(player);
            }
            bank.insertNewCombination(newWord);
            decayingWords.insertNewCombination(newWord);
            player.increaseScore(lettersUsed.size());
            return true;
        }
        throw new IOException("Word not in dictionary");
    }

    public void checkDecayingWords() {
        ArrayList<WordCombination> expiredWords = decayingWords.getExpiredCombinations();
        for (WordCombination w : expiredWords) {
            for (Letter l : w.getLetters()) {
                if (l.getDecayTime().isPresent()) {
                    if (l.getDecayTime().get().isBefore(LocalTime.now())) {
                        l.setUnoccupied();
                    }
                } else {
                    l.setUnoccupied();
                }
            }
        }
    }

    public void insertUsedWord(ArrayList<Letter> letters) {
        WordCombination newWord = new WordCombination(letters);
        newWord.addLetterExpiry();
        bank.insertNewCombination(newWord);
        decayingWords.insertNewCombination(newWord);
        for (Letter l : letters) {
            l.setPlayer(player);
        }
        player.increaseScore(letters.size());
    }

    public boolean checkIfWordAlreadyUsed(ArrayList<Letter> letters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Letter l : letters) {
            stringBuilder.append(l.getValue());
        }
        String word = String.valueOf(stringBuilder);
        for (WordCombination w : bank.getCombinations()) {
            if (Objects.equals(w.getWord(), word)) {
                return true;
            }
        }
        return false;
    }

}
