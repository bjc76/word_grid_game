package uk.ac.cam.bjc76.boggle.domain;

import java.io.IOException;
import java.util.ArrayList;

public class WordGrid {
    private int dimension;
    private ArrayList<Letter> letters;

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

    public void printGrid() {
        for (int i=0; i<dimension; i++) {
            System.out.print(" | ");
            for (int j=0; j<dimension; j++) {
                System.out.print(getLetter(j,i).getValue() + " ");
            }
            System.out.print("|\n");
        }
    }



}
