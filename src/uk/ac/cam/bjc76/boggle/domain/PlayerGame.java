package uk.ac.cam.bjc76.boggle.domain;

import java.sql.*;
import java.util.ArrayList;

public class PlayerGame {
    private WordGrid grid;
    private DecayQueue decayingWords = new DecayQueue();
    private WordBank bank = new WordBank();
    private Player player;

    public PlayerGame(Player player, WordGrid grid) throws SQLException {
        this.player = player;
        this.grid = grid;
    }

    public void goAttempt(ArrayList<Letter> lettersUsed) throws SQLException {
        StringBuilder word = new StringBuilder();
        for (Letter l : lettersUsed) {
            word.append(l.getValue());
        }
        if (Validator.checkWordIsValid(String.valueOf(word).toLowerCase())) {
            WordCombination newWord = new WordCombination(lettersUsed);
            boolean validLettersUsed = true;
            for (Letter l : newWord.getLetters()) {
                if (!l.validPlayerUsage(player)) {
                    validLettersUsed = false;
                    System.out.println("Word can't be created as squares are already occupied");
                }
            }
            if (validLettersUsed) {
                for (Letter l : newWord.getLetters()) {
                    l.setPlayer(player);
                }
                bank.insertNewCombination(newWord);
                decayingWords.insertNewCombination(newWord);
                player.increaseScore(lettersUsed.toArray().length);
                System.out.println("Valid input combination for " + player.getName());
            }
        }
    }

    public void checkDecayingWords() {
        ArrayList<WordCombination> expiredWords = decayingWords.getExpiredCombinations();
        for (WordCombination w : expiredWords) {
            for (Letter l : w.getLetters()) {
                l.setUnoccupied();
            }
        }
    }


}
