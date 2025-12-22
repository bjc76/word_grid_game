package uk.ac.cam.bjc76.boggle.domain;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class MainTest {

    public final void main(String[] args) throws Exception {
        System.out.println("Starting game...\n");
        ArrayList<String> l = new ArrayList<>();
        Game game = new Game(5, LetterGenerator.generate(25));
        game.play();
    }


}
