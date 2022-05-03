/*
 * File: DraggableUMLClassView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.uml.SequenceObjects;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Class for draggable diagrams in MainView
 */
public class UMLSequenceObjectView extends VBox {
    /**
     * Constructor - creates one new class in View
     *
     * @param sequenceObjects class from model
     */
    public UMLSequenceObjectView(SequenceObjects sequenceObjects) {
        super();
        this.getStyleClass().add("class-box");
        this.setTranslateY(10);

        Label title = new Label();
        title.setText(sequenceObjects.getObjectName() + ":" + sequenceObjects.getClassName());
        title.getStyleClass().add("class-box-title");
        this.getChildren().add(title);
    }
}
