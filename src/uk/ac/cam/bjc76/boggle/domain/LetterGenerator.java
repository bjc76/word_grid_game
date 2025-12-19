package uk.ac.cam.bjc76.boggle.domain;

import java.util.ArrayList;
import java.util.Random;

public class LetterGenerator {


    public static ArrayList<String> generate(int count) {
        char[] LETTER_POOL =
            "EEEEEEEEEEEEAAAAAAAAAIIIIIIIIIIOOOOOOOONNNNNNRRRRRRTTTTTTLLLLSSSSUUUUDDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ"
                    .toCharArray();
        ArrayList<String> letters = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String c = String.valueOf(LETTER_POOL[random.nextInt(LETTER_POOL.length)]);
            letters.add(c);
        }
        return letters;
    }

}

