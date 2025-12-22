package uk.ac.cam.bjc76.boggle.domain;

import java.io.IOException;
import java.util.ArrayList;

public class WordGrid {
    private final int dimension;
    private final ArrayList<Letter> letters;

    public WordGrid(int dimension, ArrayList<Letter> letters) throws IOException {
        this.dimension = dimension;
        this.letters = letters;
        if (letters.size() != dimension * dimension) {
            throw new IOException("Dimensions don't match letters provided");
        }
    }

    public int getDimension() {
        return dimension;
    }

    public Letter getLetter(int x, int y) {
        return letters.get(x + dimension * y);
    }

    public Letter getLetter(int index) {
        return letters.get(index);
    }

    public ArrayList<Letter> getLetters() {
        return letters;
    }

    public int indexLetter(Letter letter) {
        return letters.indexOf(letter);
    }

    public ArrayList<Letter> getSelectedLetters() {
        ArrayList<Letter> selectedLetters = new ArrayList<>();
        for (Letter l : letters) {
            if (l.isSelected()) {
                selectedLetters.add(l);
            }
        }
        return selectedLetters;
    }

    public void printGrid() {
        for (int i=0; i<dimension; i++) {
            System.out.print(" | ");
            for (int j=0; j<dimension; j++) {
                System.out.print(getLetter(j,i).getValue() + " ");
            }
            System.out.print("|\n");
        }
    }

    public boolean checkLettersAreJoined(ArrayList<Letter> wordLetters) {
        ArrayList<Integer> validIndexes = new ArrayList<>();
        int currentIndex = letters.indexOf(wordLetters.getFirst());
        for (Letter l : wordLetters.subList(1,wordLetters.size())) {
            validIndexes.addAll(getEdges(currentIndex));
            if (currentIndex >= dimension) {
                validIndexes.addAll(getEdges(currentIndex-dimension));
                validIndexes.add(currentIndex-dimension);
            }
            if (currentIndex + dimension < dimension * dimension){
                validIndexes.addAll(getEdges(currentIndex+dimension));
                validIndexes.add(currentIndex+dimension);
            }

            boolean isValid = false;
            currentIndex = letters.indexOf(l);

            for (int i : validIndexes) {
                System.out.println(i);
            }

            for (Integer i : validIndexes) {
                if (i == currentIndex) {
                    isValid = true;
                }
            }
            if (!isValid) {
                return false;
            }
            validIndexes.clear();
        }
        return true;
    }

    private ArrayList<Integer> getEdges(int currentIndex) {
        ArrayList<Integer> validIndexes = new ArrayList<>();
        if (currentIndex % dimension != 0) {
            validIndexes.add(currentIndex - 1);
        }
        if (currentIndex % dimension != dimension - 1) {
            validIndexes.add(currentIndex + 1);
        }
        return validIndexes;
    }




}
