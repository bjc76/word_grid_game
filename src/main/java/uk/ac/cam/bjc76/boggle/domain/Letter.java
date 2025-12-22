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
    private Optional<LocalTime> decayExpiry = Optional.empty();


    public Letter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isOwned() {
        return currentPlayer.isPresent();
    }

    public void setDecayExpiry(LocalTime decayExpiry) {
        this.decayExpiry = Optional.ofNullable(decayExpiry);
    }

    public Optional<LocalTime> getDecayTime() {
        return decayExpiry;
    }

    public boolean validPlayerUsage(Player player) {
        return currentPlayer.map(player1 -> Objects.equals(player, player1)).orElse(true);
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
        decayExpiry = Optional.empty();
    }

    public boolean checkOwnedByOtherPlayer(Player player) {
        return currentPlayer.filter(player1 -> !Objects.equals(player, player1)).isPresent();
    }

    public String toString() {
        return value;
    }

}
