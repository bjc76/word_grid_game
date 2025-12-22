package uk.ac.cam.bjc76.boggle.domain;

import java.time.LocalTime;
import java.util.ArrayList;

public class WordCombination {
    private final LocalTime decayExpiry;
    private final ArrayList<Letter> letters;


    public WordCombination(ArrayList<Letter> letters) {
        this.letters = letters;
        decayExpiry = LocalTime.now().plusSeconds(letters.size() * 3L);
        for (Letter l : letters) {
            l.setDecayExpiry(decayExpiry);
        }
    }

    public String getWord() {
        StringBuilder word = new StringBuilder();
        for (Letter l : letters) {
            word.append(l);
        }
        return String.valueOf(word);
    }

    public ArrayList<Letter> getLetters() {
        return letters;
    }

    public LocalTime getDecayTime() {
        return decayExpiry;
    }

}
