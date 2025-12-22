package uk.ac.cam.bjc76.boggle.domain;

public class GridCell {
    Letter cellLetter;
    Player homePlayer;

    public GridCell (Letter cellLetter, Player homePlayer) {
        this.cellLetter = cellLetter;
        this.homePlayer = homePlayer;
    }

    public String getColour() {
        if (cellLetter.checkOwnedByOtherPlayer(homePlayer)) {
            if (cellLetter.isSelected()) {
                return "#a4240d";
            } else {
                return "#f05d42";
            }
        } else if (cellLetter.isOwned()) {
            if (cellLetter.isSelected()) {
                return "#44c0eb";
            } else {
                return "#b1e5f7";
            }
        }

        else if (cellLetter.isSelected()) {
            return "#c5c7c7";
        } else {
            return "#f0f1f1";
        }
    }

    public String getValue() {
        return cellLetter.getValue();
    }

    public boolean isOwnedByOtherPlayer() {
        return cellLetter.checkOwnedByOtherPlayer(homePlayer);
    }

    public boolean isSelected() {
        return cellLetter.isSelected();
    }

    public void toggleSelected() {
        cellLetter.toggleSelected();
    }

    public void deselect() {
        cellLetter.deSelect();
    }
}
