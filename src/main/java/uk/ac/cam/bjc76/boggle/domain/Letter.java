package uk.ac.cam.bjc76.boggle.domain;

import org.springframework.cglib.core.Local;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public class Letter {
    private Optional<Player> currentPlayer = Optional.empty();
    private String value;
    private boolean isSelected = false;
    private LocalTime selectedTime = LocalTime.now();


    public Letter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean validPlayerUsage(Player player) {
        if (!currentPlayer.isPresent()) {
            return true;
        } else if (Objects.equals(player, currentPlayer)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void toggleSelected() {
        isSelected = !isSelected;
        if (isSelected) {
            selectedTime = LocalTime.now();
        }
    }
    public void deSelect() {
        isSelected = false;
    }

    public LocalTime getSelectedTime() {
        return selectedTime;
    }

    public void setPlayer(Player player) {
        currentPlayer = Optional.of(player);
    }

    public void setUnoccupied() {
        currentPlayer = Optional.empty();
    }

    public boolean checkOwnedByOtherPlayer(Player player) {
        if (currentPlayer.isPresent()) {
            return !Objects.equals(player, currentPlayer);
        }
        return false;
    }

    public String toString() {
        return value;
    }

}
