package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import uk.ac.cam.bjc76.boggle.domain.GameController;
import uk.ac.cam.bjc76.boggle.domain.GridCell;
import uk.ac.cam.bjc76.boggle.domain.Letter;

import java.util.ArrayList;


@Route("")
public class GridView extends VerticalLayout {
    private GameController gameController;
    private ArrayList<CellButton> buttonList = new ArrayList<>();
    private Registration broadcasterRegistration;

    public GridView(GameController gameController) {
        this.gameController = gameController;
        VerticalLayout boardLayout = new VerticalLayout();
        HorizontalLayout currentRow = new HorizontalLayout();
        int count = 0;
        for (Letter l : gameController.getLettersFromGrid()){
            CellButton button = new CellButton(new GridCell(l, gameController.getThisPlayer()));
            buttonList.add(button);
            currentRow.add(button.getButton());
            if (++count % gameController.getGridDimensions() == 0) {
                boardLayout.add(currentRow);
                currentRow = new HorizontalLayout();
            }
        }
        add(boardLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = GameEventBroadcaster.register(ui, message -> {
            ui.access(() -> {
                gameController.handleUpdate(message);
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

}
