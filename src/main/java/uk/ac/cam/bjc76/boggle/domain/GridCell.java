package uk.ac.cam.bjc76.boggle.domain;

public class GridCell {
    Letter cellLetter;
    boolean selected = false;
    Player homePlayer;

    public GridCell (Letter cellLetter, Player homePlayer) {
        this.cellLetter = cellLetter;
        this.homePlayer = homePlayer;
    }

    public String getColour() {
        if (cellLetter.checkOwnedByOtherPlayer(homePlayer)) {
            if (selected) {
                return "#";
            } else {
                return "#";
            }
        } else if (selected) {
            return "#";
        } else {
            return "#";
        }
    }

    public String getValue() {
        return cellLetter.getValue();
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        selected = !selected;
    }

    public void deselect() {
        selected = false;
    }
}
