package uk.ac.cam.bjc76.boggle.domain;

import java.util.Objects;
import java.util.Optional;

public class Letter {
    private Optional<Player> currentPlayer = Optional.empty();
    private String value;


    public Letter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean validPlayerUsage(Player player) {
        if (currentPlayer.isEmpty()) {
            return true;
        } else if (Objects.equals(player, currentPlayer)) {
            return true;
        } else {
            return false;
        }
    }

    public void setPlayer(Player player) {
        currentPlayer = Optional.of(player);
    }

    public void setUnoccupied() {
        currentPlayer = Optional.empty();
    }
}
