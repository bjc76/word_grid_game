package uk.ac.cam.bjc76.boggle.domain;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class WordBank {
    private ArrayList<WordCombination> wordBank = new ArrayList<>();

    public void insertNewCombination(WordCombination w) {
        wordBank.add(w);
    }

    public ArrayList<WordCombination> getCombinations() {
        return wordBank;
    }


}
