package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.button.Button;
import uk.ac.cam.bjc76.boggle.domain.GridCell;

public class CellButton {
    GridCell cellController;
    Button button;

    public CellButton (GridCell cellController) {
        this.cellController = cellController;
        button = new Button(cellController.getValue());
        button.getStyle().set("background-image", "none");
        button.setWidth("60px");
        button.setHeight("60px");
        button.addClickListener(e -> {
            cellController.toggleSelected();
            updateButtonColour();
                }
        );
        updateButtonColour();
    }

    public Button getButton() {
        return button;
    }

    public boolean isSelected() {
        return cellController.isSelected();
    }

    public void deselect() {
        cellController.deselect();
        updateButtonColour();
    }

    private void updateButtonColour() {
        button.getStyle().set("background-color", cellController.getColour());
        if (cellController.isSelected() | cellController.isOwnedByOtherPlayer()) {
            button.getStyle().set("color", "white");
        } else {
            button.getStyle().set("color", "black");
        }
    }

}
