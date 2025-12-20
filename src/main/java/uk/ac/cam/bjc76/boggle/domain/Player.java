package uk.ac.cam.bjc76.boggle.domain;

public class Player {
    private final String name;
    private final String id;
    private int score = 0;

    public Player(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public void increaseScore(int value) {
        score += value;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
