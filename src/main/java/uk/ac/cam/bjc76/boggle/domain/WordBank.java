package uk.ac.cam.bjc76.boggle.domain;

import java.util.ArrayList;

public class WordBank {
    private final ArrayList<WordCombination> wordBank = new ArrayList<>();

    public void insertNewCombination(WordCombination w) {
        wordBank.addFirst(w);
    }

    public ArrayList<WordCombination> getCombinations() {
        return wordBank;
    }

    public String getMostRecentWord() {
        return wordBank.getFirst().getWord();
    }


}
