/*
 * File: PropertiesView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Class for properties view (Properties section in menu)
 */
public class PropertiesView extends VBox {
    /**
     * Adds one line to the properties section.
     * @param property name of property, data description
     * @param text data-value (in String)
     */
    public void addPropertyLine(String property, String text) {
        BorderPane line = new BorderPane();
        Label property_lab = new Label(property);
        Label text_lab = new Label(text);

        line.getStyleClass().add("box-property-line");

        property_lab.getStyleClass().add("property-name");
        property_lab.setAlignment(Pos.TOP_LEFT);
        property_lab.setTextAlignment(TextAlignment.LEFT);
        text_lab.setAlignment(Pos.TOP_RIGHT);
        text_lab.setTextAlignment(TextAlignment.RIGHT);

        line.setLeft(property_lab);
        line.setRight(text_lab);
        this.getChildren().add(line);
    }

    /**
     * Clears properties section
     */
    public void resetProperties() {
        this.getChildren().clear();
    }
}
