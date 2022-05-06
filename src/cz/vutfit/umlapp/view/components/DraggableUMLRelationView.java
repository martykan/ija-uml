/*
 * File: DraggableUMLRelationView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.uml.Relationships;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DraggableUMLRelationView extends AnchorPane {

    final DraggableUMLClassView node1;
    final DraggableUMLClassView node2;
    final AtomicReference<Double> totalZoom;
    final Relationships relationship;

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
        if (this.node1.getWidth() == 0) {
            // Node not rendered yet
            Platform.runLater(DraggableUMLRelationView.this::drawLine);
            return;
        }

        double node1CenterX = this.node1.getTranslateX() + this.node1.getWidth() / 2;
        double node1CenterY = this.node1.getTranslateY() + this.node1.getHeight() / 2;
        double node2CenterX = this.node2.getTranslateX() + this.node2.getWidth() / 2;
        double node2CenterY = this.node2.getTranslateY() + this.node2.getHeight() / 2;

        boolean toSelf = node1.getClassDiagram().getID() == node2.getClassDiagram().getID();
        if (toSelf && relationship.lineMidpoints.isEmpty()) {
            this.relationship.lineMidpoints.add(new Point2D(node1CenterX - 10, node1.getTranslateY() + node1.getHeight() + 50));
            this.relationship.lineMidpoints.add(new Point2D(node1CenterX + 10, node1.getTranslateY() + node1.getHeight() + 50));
        }

        this.getChildren().clear();
        List<Line> lines = new ArrayList<>();

        for (int i = 0; i <= this.relationship.lineMidpoints.size(); i++) {
            int finalI = i;
            // Line
            Line line = new Line();
            if (i == 0) {
                line.setStartX(node1CenterX);
                line.setStartY(node1CenterY);
            } else {
                line.setStartX(this.relationship.lineMidpoints.get(i - 1).getX());
                line.setStartY(this.relationship.lineMidpoints.get(i - 1).getY());
            }
            if (i == this.relationship.lineMidpoints.size()) {
                line.setEndX(node2CenterX);
                line.setEndY(node2CenterY);
            } else {
                line.setEndX(this.relationship.lineMidpoints.get(i).getX());
                line.setEndY(this.relationship.lineMidpoints.get(i).getY());
            }
            line.setStrokeWidth(2);
            line.setOnMouseEntered(event -> {
                line.setStrokeWidth(4);
            });
            line.setOnMouseExited(event -> {
                line.setStrokeWidth(2);
            });
            line.setOnMousePressed(event -> {
                this.relationship.lineMidpoints.add(finalI, new Point2D(event.getX(), event.getY()));
                this.drawLine();
            });
            lines.add(line);
        }
        this.getChildren().addAll(lines);

        // Labels
        double dx = node2.getTranslateX() - node1.getTranslateX();
        double dy = node2.getTranslateY() - node1.getTranslateY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle <= -90 || angle >= 90) {
            angle += 180;
        }
        Rotate rotate = new Rotate();
        rotate.setAngle(angle);
        rotate.setPivotX(0);
        rotate.setPivotY(0);

        Label labelCenter = new Label(this.relationship.getName());
        labelCenter.setAlignment(Pos.CENTER);
        labelCenter.setTextAlignment(TextAlignment.CENTER);
        if (node1.getTranslateX() < node2.getTranslateX()) {
            labelCenter.setTranslateX(node1CenterX);
            labelCenter.setTranslateY(node1CenterY);
        } else {
            labelCenter.setTranslateX(node2CenterX);
            labelCenter.setTranslateY(node2CenterY);
        }
        labelCenter.setPrefWidth(Math.sqrt(Math.pow(node2.getTranslateX() - node1.getTranslateX(), 2) + Math.pow(node2.getTranslateY() - node1.getTranslateY(), 2)));
        labelCenter.getTransforms().add(rotate);
        this.getChildren().add(labelCenter);
        Label labelStart = new Label(this.relationship.getFromDesc());
        Point2D startIntersect = UMLRelationArrow.lineIntersectionOnRect(
                node1.getWidth(),
                node1.getHeight(),
                new Point2D(node1CenterX, node1CenterY),
                new Point2D(node2CenterX, node2CenterY)
        );
        Label labelEnd = new Label(this.relationship.getToDesc());
        Point2D endIntersect = UMLRelationArrow.lineIntersectionOnRect(
                node2.getWidth(),
                node2.getHeight(),
                new Point2D(node2CenterX, node2CenterY),
                new Point2D(node1CenterX, node1CenterY)
        );
        labelStart.setPrefWidth(100);
        labelEnd.setPrefWidth(100);
        if (node1CenterX < node2CenterX) {
            labelStart.setTranslateX(startIntersect.getX() + 15);
            labelStart.setAlignment(Pos.CENTER_LEFT);
            labelEnd.setTranslateX(endIntersect.getX() - 120);
            labelEnd.setAlignment(Pos.CENTER_RIGHT);
        } else {
            labelEnd.setTranslateX(endIntersect.getX() + 15);
            labelEnd.setAlignment(Pos.CENTER_LEFT);
            labelStart.setTranslateX(startIntersect.getX() - 120);
            labelStart.setAlignment(Pos.CENTER_RIGHT);
        }
        if (node1CenterY < node2CenterY) {
            labelStart.setTranslateY(startIntersect.getY() + 10);
            labelEnd.setTranslateY(endIntersect.getY() - 20);
        } else {
            labelStart.setTranslateY(startIntersect.getY() - 20);
            labelEnd.setTranslateY(endIntersect.getY() + 10);
        }
        this.getChildren().add(labelStart);
        this.getChildren().add(labelEnd);

        // Arrows
        final UMLRelationArrow arrowStart = new UMLRelationArrow(this.node1, new Point2D(lines.get(0).getEndX(), lines.get(0).getEndY()), this.relationship.getType(), false);
        final UMLRelationArrow arrowEnd = new UMLRelationArrow(this.node2, new Point2D(lines.get(lines.size() - 1).getStartX(), lines.get(lines.size() - 1).getStartY()), this.relationship.getType(), true);
        this.getChildren().add(arrowStart);
        this.getChildren().add(arrowEnd);

        // Midpoint indicators
        for (int i = 0; i <= this.relationship.lineMidpoints.size(); i++) {
            int finalI = i;
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
                    posX = Math.round(posX / 5) * 5;
                    posY = Math.round(posY / 5) * 5;
                    event.consume();
                    if (posX == circle.getTranslateX() && posY == circle.getTranslateY()) return;
                    circle.setTranslateX(posX);
                    circle.setTranslateY(posY);

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
