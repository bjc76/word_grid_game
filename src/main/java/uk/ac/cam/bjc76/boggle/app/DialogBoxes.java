package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;


public class DialogBoxes {
    public static void showGameOverDialog(boolean userWon, int userScore, int opponentScore, Runnable callback) {
        Dialog dialog = new Dialog();

        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);

        String titleText = userWon ? "VICTORY!" : (userScore == opponentScore ? "DRAW" : "DEFEAT");
        H2 title = new H2(titleText);

        if (userWon) {
            title.getStyle().set("color", "#28b62c");
        } else if (userScore == opponentScore) {
            title.getStyle().set("color", "#555");
        } else {
            title.getStyle().set("color", "#ff3333");
        }
        title.getStyle().set("margin-top", "0");

        Span scoreLabel = new Span(userScore + " - " + opponentScore);
        scoreLabel.getStyle().set("font-size", "48px");
        scoreLabel.getStyle().set("font-weight", "bold");
        scoreLabel.getStyle().set("margin", "10px 0");

        Span subText = new Span(userWon ? "Great job!" : "Better luck next time.");
        subText.getStyle().set("color", "gray");

        Button exitButton = new Button("Exit");
        exitButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        exitButton.setWidthFull();
        exitButton.addClickListener(e -> {
            dialog.close();
            callback.run();
        });

        dialogLayout.add(title, scoreLabel, subText, exitButton);
        dialog.add(dialogLayout);

        dialog.setWidth("400px");
        dialog.setMaxWidth("90vw");

        dialog.open();
    }

    public static void showGameRequestDialog(String requesterName, Runnable onAccept, Runnable onDecline) {
        Dialog dialog = new Dialog();

        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);

        H2 title = new H2("Game Request");
        title.getStyle().set("margin-top", "0");

        Span message = new Span("wants to play Boggle with you!");
        message.getStyle().set("color", "gray");

        Span nameSpan = new Span(requesterName);
        nameSpan.getStyle().set("font-size", "24px");
        nameSpan.getStyle().set("font-weight", "bold");
        nameSpan.getStyle().set("color", "#2c3e50"); // Dark Blue/Grey
        nameSpan.getStyle().set("margin", "10px 0");

        Button acceptButton = new Button("Accept");
        acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        acceptButton.setWidthFull();
        acceptButton.addClickListener(e -> {
            dialog.close();
            onAccept.run();
        });

        Button declineButton = new Button("Decline");
        declineButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        declineButton.setWidthFull();
        declineButton.addClickListener(e -> {
            dialog.close();
            onDecline.run();
        });

        dialogLayout.add(title, nameSpan, message, acceptButton, declineButton);
        dialog.add(dialogLayout);

        dialog.setWidth("350px");
        dialog.setMaxWidth("90vw");

        dialog.open();
    }

    public static Dialog showWaitingDialog(Runnable onClose) {
        Dialog dialog = new Dialog();

        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(true);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setWidth("150px");

        Span message = new Span("Waiting for confirmation...");
        message.getStyle().set("color", "gray");
        message.getStyle().set("font-size", "16px");

        Button exitButton = new Button("Quit");
        exitButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        exitButton.setWidthFull();
        exitButton.addClickListener(e -> {
            dialog.close();
            onClose.run();
        });

        layout.add(progressBar, message, exitButton);
        dialog.add(layout);

        dialog.open();

        return dialog;
    }

    public static void showDisconnectedDialog(Runnable callback) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(false);
        layout.setSpacing(true);

        H2 title = new H2("Opponent Disconnected");
        title.getStyle().set("color", "#ff3333");
        title.getStyle().set("margin-top", "0");

        Span message = new Span("The other player has left the game.");

        Button exitButton = new Button("Return to Menu");
        exitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exitButton.setWidthFull();
        exitButton.addClickListener(e -> {
            dialog.close();
            callback.run();
        });

        layout.add(title, message, exitButton);
        dialog.add(layout);

        dialog.setWidth("350px");
        dialog.setMaxWidth("90vw");

        dialog.open();
    }
}
