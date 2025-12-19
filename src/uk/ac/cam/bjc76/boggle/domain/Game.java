package uk.ac.cam.bjc76.boggle.domain;

import java.sql.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Game {
    private Player otherPlayer;
    private Player thisPlayer;
    private WordGrid grid;
    private Connection conn = DriverManager.getConnection("jdbc:sqlite:data/words.db");


    public Game(String player1name, String player2name, int dimensions, ArrayList<String> gridList) throws Exception {
        thisPlayer = new Player(player1name, "#FF0000");
        otherPlayer = new Player(player2name, "#0000FF");
        ArrayList<Letter> tempLetters = new ArrayList<>();
        for (String s : gridList) {
            tempLetters.add(new Letter(s));
        }
        grid = new WordGrid(dimensions, tempLetters);
    }


    public void play() throws SQLException {
        boolean gameOver = false;
        Scanner inputListener = new Scanner(System.in);
        LocalTime endTime = LocalTime.now().plusMinutes(3);
        PlayerGame player1 = new PlayerGame(thisPlayer, grid);
        PlayerGame player2 = new PlayerGame(otherPlayer, grid);

        while (!gameOver) {
            System.out.println("Player 1 score: " + thisPlayer.getScore());
            System.out.println("Player 2 score: " + otherPlayer.getScore());

            grid.printGrid();
            long timeRemaining = LocalTime.now().until(endTime, ChronoUnit.SECONDS);
            System.out.print("Time remaining: " + timeRemaining);
            System.out.println("\nEnter a word (using single char indexes in the form xy eg. x = 2, y = 4 as 24, " +
                    "with a space between each letter, followed by the player number eg. P1");
            String input = inputListener.nextLine();
            String[] inputs = input.split(" ");
            System.out.println(input);
            String player = inputs[inputs.length - 1];
            ArrayList<Letter> lettersUsed = new ArrayList<>();
            for (int i = 0; i < inputs.length - 1; i++) {
                lettersUsed.add(grid.getLetter(inputs[i].charAt(0) - '0', inputs[i].charAt(1) - '0'));
            }
            if (Objects.equals(player, "P1")) {
                player1.goAttempt(lettersUsed);
            } else if (Objects.equals(player, "P2")) {
                player2.goAttempt(lettersUsed);
            } else {
                System.out.println("Invalid player please enter P1 or P2\n");
            }

            player1.checkDecayingWords();
            player2.checkDecayingWords();


            timeRemaining = LocalTime.now().until(endTime, ChronoUnit.SECONDS);
            if (timeRemaining < 0) {
                gameOver = true;
            }
        }
    }

    private void setLettersAsPlayer(ArrayList<Letter> letters, Player player) {
        for (Letter l : letters) {
            l.setPlayer(player);
        }
    }


}
