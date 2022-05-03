/*
 * File: DraggableUMLRelationView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.uml.Relationships;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DraggableUMLRelationView extends AnchorPane {

    DraggableUMLClassView node1;
    DraggableUMLClassView node2;
    AtomicReference<Double> totalZoom;
    Relationships relationship;

    public DraggableUMLRelationView(DraggableUMLClassView node1, DraggableUMLClassView node2, AtomicReference<Double> totalZoom, Relationships relationship) {
        super();

        this.node1 = node1;
        this.node2 = node2;
        this.totalZoom = totalZoom;
        this.relationship = relationship;

        this.setPrefWidth(10000);
        this.setPrefHeight(10000);
        this.setViewOrder(Double.MIN_VALUE);
        this.setPickOnBounds(false);

        // Update start and end points based on location
        node1.translateXProperty().addListener(event -> {
            drawLine();
        });
        node1.translateYProperty().addListener(event -> {
            drawLine();
        });
        node2.translateXProperty().addListener(event -> {
            drawLine();
        });
        node2.translateYProperty().addListener(event -> {
            drawLine();
        });

        // Defer so we can compute node width/height
        Platform.runLater(DraggableUMLRelationView.this::drawLine);
    }


    /**
     * Draw lines between classes, with midpoints
     */
    public void drawLine() {
        this.getChildren().clear();
        List<Line> lines = new ArrayList<>();

        for (int i = 0; i <= this.relationship.lineMidpoints.size(); i++) {
            int finalI = i;
            // Line
            Line line = new Line();
            if (i == 0) {
                line.setStartX(this.node1.getTranslateX() + this.node1.getWidth() / 2);
                line.setStartY(this.node1.getTranslateY() + this.node1.getHeight() / 2);
            } else {
                line.setStartX(this.relationship.lineMidpoints.get(i - 1).getX());
                line.setStartY(this.relationship.lineMidpoints.get(i - 1).getY());
            }
            if (i == this.relationship.lineMidpoints.size()) {
                line.setEndX(this.node2.getTranslateX() + this.node2.getWidth() / 2);
                line.setEndY(this.node2.getTranslateY() + this.node2.getHeight() / 2);
            } else {
                line.setEndX(this.relationship.lineMidpoints.get(i).getX());
                line.setEndY(this.relationship.lineMidpoints.get(i).getY());
            }
            line.setStrokeWidth(2);
            line.setOnMousePressed(event -> {
                this.relationship.lineMidpoints.add(finalI, new Point2D(event.getX(), event.getY()));
                this.drawLine();
            });
            lines.add(line);
        }
        this.getChildren().addAll(lines);

        final UMLRelationArrow arrowStart = new UMLRelationArrow(this.node1, new Point2D(lines.get(0).getEndX(), lines.get(0).getEndY()), this.relationship.getType(), false);
        final UMLRelationArrow arrowEnd = new UMLRelationArrow(this.node2, new Point2D(lines.get(lines.size() - 1).getStartX(), lines.get(lines.size() - 1).getStartY()), this.relationship.getType(), true);
        this.getChildren().add(arrowStart);
        this.getChildren().add(arrowEnd);

        for (int i = 0; i <= this.relationship.lineMidpoints.size(); i++) {
            int finalI = i;
            // Midpoint indicator
            if (i < this.relationship.lineMidpoints.size()) {
                Circle circle = new Circle(15, Paint.valueOf("blue"));
                circle.setTranslateX(this.relationship.lineMidpoints.get(i).getX());
                circle.setTranslateY(this.relationship.lineMidpoints.get(i).getY());
                circle.setOpacity(0);

                // Draggable
                AtomicReference<Double> originalX = new AtomicReference<>(0.0);
                AtomicReference<Double> originalY = new AtomicReference<>(0.0);
                AtomicReference<Double> initialX = new AtomicReference<>(0.0);
                AtomicReference<Double> initialY = new AtomicReference<>(0.0);
                circle.setOnMousePressed(event -> {
                    initialX.set(event.getSceneX());
                    initialY.set(event.getSceneY());
                    originalX.set(circle.getTranslateX());
                    originalY.set(circle.getTranslateY());
                });
                circle.setOnMouseDragged(event -> {
                    double posX = (event.getSceneX() - initialX.get()) / totalZoom.get() + originalX.get();
                    double posY = (event.getSceneY() - initialY.get()) / totalZoom.get() + originalY.get();
                    circle.setTranslateX(posX);
                    circle.setTranslateY(posY);
                    event.consume();

                    lines.get(finalI).setEndX(posX);
                    lines.get(finalI).setEndY(posY);
                    lines.get(finalI + 1).setStartX(posX);
                    lines.get(finalI + 1).setStartY(posY);
                    if (finalI == 0) {
                        arrowStart.updatePosition(new Point2D(posX, posY));
                    }
                    if (finalI == this.relationship.lineMidpoints.size() - 1) {
                        arrowEnd.updatePosition(new Point2D(posX, posY));
                    }
                    this.relationship.lineMidpoints.set(finalI, new Point2D(circle.getTranslateX(), circle.getTranslateY()));
                });
                circle.setOnContextMenuRequested(event -> {
                    this.relationship.lineMidpoints.remove(finalI);
                    drawLine();
                });
                circle.setOnMouseEntered(event -> {
                    circle.setOpacity(0.1);
                });
                circle.setOnMouseExited(event -> {
                    circle.setOpacity(0);
                });
                this.getChildren().add(circle);
            }
        }
    }
}
