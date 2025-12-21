package uk.ac.cam.bjc76.boggle.domain;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerGame {
    private WordGrid grid;
    private DecayQueue decayingWords = new DecayQueue();
    private WordBank bank = new WordBank();
    private Player player;

    public PlayerGame(Player player, WordGrid grid) throws SQLException {
        this.player = player;
        this.grid = grid;
    }

    public boolean goAttempt(ArrayList<Letter> lettersUsed) throws SQLException {
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
                for (Letter l : lettersUsed) {
                    l.setPlayer(player);
                }
                bank.insertNewCombination(newWord);
                decayingWords.insertNewCombination(newWord);
                player.increaseScore(lettersUsed.size());
                System.out.println("Valid input combination for " + player.getName());
                return true;
            }
        }
        return false;
    }

    public void checkDecayingWords() {
        ArrayList<WordCombination> expiredWords = decayingWords.getExpiredCombinations();
        for (WordCombination w : expiredWords) {
            for (Letter l : w.getLetters()) {
                l.setUnoccupied();
            }
        }
    }

    public void insertUsedWord(ArrayList<Letter> letters) {
        WordCombination newWord = new WordCombination(letters);
        bank.insertNewCombination(newWord);
        decayingWords.insertNewCombination(newWord);
        for (Letter l : letters) {
            l.setPlayer(player);
        }
        player.increaseScore(letters.size());
        System.out.println("Valid input combination for " + player.getName());
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
