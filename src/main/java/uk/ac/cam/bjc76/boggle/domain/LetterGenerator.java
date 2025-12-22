package uk.ac.cam.bjc76.boggle.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LetterGenerator {
    public static final List<String> BIG_BOGGLE_LETTERS = Arrays.asList(
            "A", "A", "A", "F", "R", "S",
            "A", "A", "E", "E", "E", "E",
            "A", "A", "F", "I", "R", "S",
            "A", "D", "E", "N", "N", "N",
            "A", "E", "E", "E", "E", "M",
            "A", "E", "E", "G", "M", "U",
            "A", "E", "G", "M", "N", "N",
            "A", "F", "I", "R", "S", "Y",
            "B", "J", "K", "Qu", "X", "Z",
            "C", "C", "N", "S", "T", "W",
            "C", "E", "I", "I", "L", "T",
            "C", "E", "I", "L", "P", "T",
            "C", "E", "I", "P", "S", "T",
            "D", "D", "L", "N", "O", "R",
            "D", "H", "H", "L", "O", "R",
            "D", "H", "H", "N", "O", "T",
            "D", "H", "L", "N", "O", "R",
            "E", "I", "I", "I", "T", "T",
            "E", "M", "O", "T", "T", "T",
            "E", "N", "S", "S", "S", "U",
            "F", "I", "P", "R", "S", "Y",
            "G", "O", "R", "R", "V", "W",
            "H", "I", "P", "R", "R", "Y",
            "N", "O", "O", "T", "U", "W",
            "O", "O", "O", "T", "T", "U"
    );

    public static ArrayList<String> generate(int count) {
        ArrayList<String> letters = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String c = BIG_BOGGLE_LETTERS.get(random.nextInt(BIG_BOGGLE_LETTERS.size()));
            letters.add(c);
        }
        return letters;
    }

}

