/*
 * File: ArrowHead.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class ArrowHead extends AnchorPane {
    Node arrow;
    Paint primaryColor = Paint.valueOf("black");

    public ArrowHead() {
        this.setPickOnBounds(false);
    }

    public ArrowHead(EArrowType arrowType, Paint primaryColor) {
        this();
        if (primaryColor != null)
            this.primaryColor = primaryColor;
        this.setArrowType(arrowType);
        this.show();
    }

    public ArrowHead(EArrowType arrowType) {
        this(arrowType, null);
    }

    public void setArrowType(EArrowType arrowType) {
        if (arrowType == EArrowType.BASIC) {
            Polyline polyline = new Polyline(0, 16, 0, 0, 16, 0);
            polyline.setStroke(primaryColor);
            polyline.setStrokeWidth(2);
            arrow = polyline;
        } else if (arrowType == EArrowType.TRIANGLE) {
            Polygon polygon = new Polygon(0, 0, 0, 16, 16, 0);
            polygon.setFill(Paint.valueOf("white"));
            polygon.setStroke(primaryColor);
            polygon.setStrokeWidth(2);
            arrow = polygon;
        } else if (arrowType == EArrowType.TRIANGLE_FILLED) {
            Polygon polygon = new Polygon(0, 0, 0, 16, 16, 0);
            polygon.setFill(primaryColor);
            polygon.setStroke(primaryColor);
            polygon.setStrokeWidth(2);
            arrow = polygon;
        } else if (arrowType == EArrowType.SQUARE_FILLED) {
            arrow = new Rectangle(16, 16, primaryColor);
        } else if (arrowType == EArrowType.SQUARE) {
            Rectangle rectangle = new Rectangle(16, 16, Paint.valueOf("white"));
            rectangle.setStroke(primaryColor);
            rectangle.setStrokeWidth(2);
            arrow = rectangle;
        } else if (arrowType == EArrowType.CROSS) {
            Polyline polyline = new Polyline(0, 16, 32, 16, 16, 16, 16, 0, 16, 32);
            polyline.setStroke(Paint.valueOf("red"));
            polyline.setStrokeWidth(2);
            polyline.setTranslateX(24);
            arrow = polyline;
        } else {
            arrow = null;
        }
    }

    public void show() {
        this.getChildren().clear();
        this.getChildren().add(arrow);
    }

    public void setAngle(double angle) {
        Rotate rotate = new Rotate();
        rotate.setAngle(angle);
        rotate.setPivotX(0);
        rotate.setPivotY(0);
        arrow.getTransforms().clear();
        arrow.getTransforms().add(rotate);
    }

    public enum EArrowType {
        NONE,
        BASIC,
        TRIANGLE,
        TRIANGLE_FILLED,
        SQUARE,
        SQUARE_FILLED,
        CROSS
    }
}
