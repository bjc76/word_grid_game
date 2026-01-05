package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import uk.ac.cam.bjc76.boggle.domain.GameController;
import uk.ac.cam.bjc76.boggle.domain.GridCell;
import uk.ac.cam.bjc76.boggle.domain.Letter;
import uk.ac.cam.bjc76.boggle.ui.Styles;

import java.util.ArrayList;

public class ScreenLayout extends VerticalLayout{
    private Span scoreDisplay;
    private Span opponentScoreDisplay;
    private Span timeDisplay;
    private VerticalLayout myWordsLayout;
    private VerticalLayout opponentWordsLayout;

    public Span getScoreDisplay() {
        return scoreDisplay;
    }

    public Span getOpponentScoreDisplay() {
        return opponentScoreDisplay;
    }

    public Span getTimeDisplay() {
        return timeDisplay;
    }

    public VerticalLayout getMyWordsLayout() {
        return myWordsLayout;
    }

    public VerticalLayout getOpponentWordsLayout() {
        return opponentWordsLayout;
    }

    public ScreenLayout (boolean isMobile, GameController gameController, ArrayList<CellButton> buttonList, Runnable handleSubmit) {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        scoreDisplay = new Span();
        opponentScoreDisplay = new Span();
        timeDisplay = new Span(gameController.getTimeRemaining());
        timeDisplay.getStyle().set("font-weight", "bold");


        // alternate displays for mobile and desktop as full content doesn't fit on mobile.
        if (isMobile) {
            headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            headerLayout.setPadding(false);

            HorizontalLayout scoreGroup = new HorizontalLayout();
            scoreGroup.setSpacing(false);
            scoreGroup.setAlignItems(FlexComponent.Alignment.CENTER);

            scoreDisplay.setText("0");
            scoreDisplay.getStyle().set("color", "green").set("font-weight", "bold").set("font-size", "1.5rem");

            Span sep = new Span(" - ");
            sep.getStyle().set("font-weight", "bold").set("font-size", "1.5rem").set("margin", "0 10px");

            opponentScoreDisplay.setText("0");
            opponentScoreDisplay.getStyle().set("color", "red").set("font-weight", "bold").set("font-size", "1.5rem");

            scoreGroup.add(scoreDisplay, sep, opponentScoreDisplay);
            timeDisplay.getStyle().set("font-size", "1.2rem");

            headerLayout.add(scoreGroup, timeDisplay);

            myWordsLayout = null;
            opponentWordsLayout = null;
        } else {
            headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
            scoreDisplay.setText("Your Score: 0");
            opponentScoreDisplay.setText("Opponent Score: 0");
            Styles.scoreStyle(scoreDisplay);
            Styles.scoreStyle(opponentScoreDisplay);
            Styles.scoreStyle(timeDisplay);
            headerLayout.add(scoreDisplay, timeDisplay, opponentScoreDisplay);

            myWordsLayout = new VerticalLayout(new Span("My Words:"));
            myWordsLayout.setWidth("200px");
            opponentWordsLayout = new VerticalLayout(new Span("Opponent's Words:"));
            opponentWordsLayout.setWidth("200px");
        }

        Div boardContainer = new Div();
        boardContainer.getStyle().set("display", "grid");
        boardContainer.getStyle().set("grid-template-columns", "repeat(" + gameController.getGridDimensions() + ", 1fr)");
        boardContainer.getStyle().set("gap", "4px");
        boardContainer.getStyle().set("width", "100%"); // Fill the gameContainer
        boardContainer.getStyle().set("box-sizing", "border-box"); // Ensure padding doesn't cause overflow

        for (Letter l : gameController.getLettersFromGrid()) {
            CellButton cellBtn = new CellButton(new GridCell(l, gameController.getThisPlayer()));
            Button btn = cellBtn.getButton();

            btn.setWidth("100%");
            btn.setHeight("auto");
            btn.getStyle().set("aspect-ratio", "1 / 1");
            btn.getStyle().set("padding", "0");

            btn.getStyle().set("min-width", "0");
            btn.getStyle().set("font-size", isMobile ? "1.2em" : "1.5em");
            btn.getStyle().set("margin", "0");

            boardContainer.add(btn);
            buttonList.add(cellBtn);
        }

        Button submitWordButton = new Button("Submit");
        submitWordButton.addClickListener(e -> handleSubmit.run());
        submitWordButton.addThemeName("primary");
        submitWordButton.getStyle().set("margin-top", "15px");

        VerticalLayout gameContainer = new VerticalLayout();
        gameContainer.setPadding(false);
        gameContainer.setSpacing(false);
        gameContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        gameContainer.add(boardContainer, submitWordButton);

        if (isMobile) {
            gameContainer.setWidth("95%");
            gameContainer.setMaxWidth("400px");
            submitWordButton.setWidth("50%");
        } else {
            gameContainer.setWidth("600px");
            submitWordButton.setWidth("200px");
        }

        if (isMobile) {
            Div spacer = new Div();
            spacer.setHeight("40px");
            spacer.setWidthFull();

            setSizeFull();
            setPadding(true);
            setJustifyContentMode(FlexComponent.JustifyContentMode.START);
            setAlignItems(FlexComponent.Alignment.CENTER);
            add(spacer, headerLayout, gameContainer);
        } else {
            HorizontalLayout mainContainer = new HorizontalLayout();
            mainContainer.setWidthFull();
            mainContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            VerticalLayout centerLayout = new VerticalLayout();
            centerLayout.setWidth("auto");
            centerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            centerLayout.add(gameContainer);

            mainContainer.add(myWordsLayout, centerLayout, opponentWordsLayout);
            add(headerLayout, mainContainer);
        }
    }


}
