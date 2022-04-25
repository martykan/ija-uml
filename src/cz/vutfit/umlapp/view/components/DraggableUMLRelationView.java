/*
 * File: DraggableUMLRelationView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

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
    List<Point2D> midpoints = new ArrayList<>();
    AtomicReference<Double> totalZoom;

    public DraggableUMLRelationView(DraggableUMLClassView node1, DraggableUMLClassView node2, AtomicReference<Double> totalZoom) {
        super();

        this.node1 = node1;
        this.node2 = node2;
        this.totalZoom = totalZoom;

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
        for (int i = 0; i <= midpoints.size(); i++) {
            int finalI = i;
            // Line
            Line line = new Line();
            if (i == 0) {
                line.setStartX(this.node1.getTranslateX() + this.node1.getWidth() / 2);
                line.setStartY(this.node1.getTranslateY() + this.node1.getHeight() / 2);
            } else {
                line.setStartX(midpoints.get(i - 1).getX());
                line.setStartY(midpoints.get(i - 1).getY());
            }
            if (i == midpoints.size()) {
                line.setEndX(this.node2.getTranslateX() + this.node2.getWidth() / 2);
                line.setEndY(this.node2.getTranslateY() + this.node2.getHeight() / 2);
            } else {
                line.setEndX(midpoints.get(i).getX());
                line.setEndY(midpoints.get(i).getY());
            }
            line.setStrokeWidth(2);
            line.setOnMousePressed(event -> {
                midpoints.add(finalI, new Point2D(event.getX(), event.getY()));
                this.drawLine();
            });
            lines.add(line);
            this.getChildren().add(line);
        }

        for (int i = 0; i <= midpoints.size(); i++) {
            int finalI = i;
            // Midpoint indicator
            if (i < midpoints.size()) {
                Circle circle = new Circle(15, Paint.valueOf("blue"));
                circle.setTranslateX(midpoints.get(i).getX());
                circle.setTranslateY(midpoints.get(i).getY());
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

                    midpoints.set(finalI, new Point2D(circle.getTranslateX(), circle.getTranslateY()));
                });
                circle.setOnContextMenuRequested(event -> {
                    midpoints.remove(finalI);
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
