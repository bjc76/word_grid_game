package uk.ac.cam.bjc76.boggle.domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DecayQueue {
    private PriorityQueue<WordCombination> decayQueue = new PriorityQueue<>(Comparator.comparing(WordCombination::getDecayTime));

    public ArrayList<WordCombination> getExpiredCombinations() {
        LocalTime presentTime = LocalTime.now();
        ArrayList<WordCombination> expiredCombinations = new ArrayList<>();

        while (!decayQueue.isEmpty()) {
            if (decayQueue.peek().getDecayTime().isBefore(presentTime)) {
                expiredCombinations.add(decayQueue.remove());
            } else {
                break;
            }
        }

        return expiredCombinations;
    }

    public void insertNewCombination(WordCombination w) {
        decayQueue.add(w);
    }


}
