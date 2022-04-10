package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.Methods;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DraggableUMLClassView extends VBox {
    public DraggableUMLClassView(ClassDiagram classDiagram, AtomicReference<Double> totalZoom) {
        super();
        this.getStyleClass().add("class-box");
        this.setTranslateX(classDiagram.positionX);
        this.setTranslateY(classDiagram.positionY);

        Label title = new Label();
        title.setText(classDiagram.getName());
        title.getStyleClass().add("class-box-title");
        this.getChildren().add(title);

        Label attributes = new Label();
        attributes.getStyleClass().add("class-box-attribs");
        if (!classDiagram.getAttribs().isEmpty()) {
            attributes.setText(classDiagram.getAttribs().stream().map(Attributes::getNameWithPrefix).collect(Collectors.joining("\n")));
        } else {
            attributes.getStyleClass().add("class-box-empty");
        }
        this.getChildren().add(attributes);

        Label methods = new Label();
        methods.getStyleClass().add("class-box-methods");
        if (!classDiagram.getMethods().isEmpty()) {
            methods.setText(classDiagram.getMethods().stream().map(Methods::getNameWithPrefix).collect(Collectors.joining("\n")));
        } else {
            methods.getStyleClass().add("class-box-empty");
        }
        this.getChildren().add(methods);

        // Draggable
        AtomicReference<Double> originalX = new AtomicReference<>(0.0);
        AtomicReference<Double> originalY = new AtomicReference<>(0.0);
        AtomicReference<Double> initialX = new AtomicReference<>(0.0);
        AtomicReference<Double> initialY = new AtomicReference<>(0.0);
        this.setOnMousePressed(event -> {
            initialX.set(event.getSceneX());
            initialY.set(event.getSceneY());
            originalX.set(this.getTranslateX());
            originalY.set(this.getTranslateY());
        });
        this.setOnMouseDragged(event -> {
            this.setTranslateX((event.getSceneX() - initialX.get()) / totalZoom.get() + originalX.get());
            this.setTranslateY((event.getSceneY() - initialY.get()) / totalZoom.get() + originalY.get());
            event.consume();
        });
    }
}
