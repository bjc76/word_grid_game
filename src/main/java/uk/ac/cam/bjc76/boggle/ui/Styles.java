package uk.ac.cam.bjc76.boggle.ui;

import com.vaadin.flow.component.html.Span;

public class Styles {
    public static void scoreStyle(Span scoreCard) {
        scoreCard.getStyle().set("border", "2px solid #333");      // Dark border
        scoreCard.getStyle().set("border-radius", "8px");          // Rounded corners
        scoreCard.getStyle().set("padding", "10px 20px");          // Space inside the box
        scoreCard.getStyle().set("background-color", "#f0f0f0");   // Light grey background
        scoreCard.getStyle().set("font-weight", "bold");           // Bold text
        scoreCard.getStyle().set("font-size", "18px");
    }
}
