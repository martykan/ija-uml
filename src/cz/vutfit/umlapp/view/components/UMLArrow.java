/*
 * File: Arrow.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.uml.ERelationType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class UMLArrow extends AnchorPane {
    VBox node;
    ERelationType type;
    boolean isEnd;

    public UMLArrow(VBox node, Point2D startPoint, ERelationType type, boolean isEnd) {
        this.node = node;
        this.type = type;
        this.isEnd = isEnd;

        this.setPickOnBounds(false);
        this.updatePosition(startPoint);
    }

    /**
     * Line intersection algorithm
     * Based on https://stackoverflow.com/questions/1585525/how-to-find-the-intersection-point-between-a-line-and-a-rectangle
     *
     * @param width  rectangle width
     * @param height rectangle height
     * @param B      center of rectangle
     * @param A      start of line
     * @return intersection point
     */
    public static Point2D lineIntersectionOnRect(double width, double height, Point2D B, Point2D A) {
        double w = width / 2;
        double h = height / 2;
        double dx = A.getX() - B.getX();
        double dy = A.getY() - B.getY();

        if (dx == 0 && dy == 0) return B;

        double tan_phi = h / w;
        double tan_theta = Math.abs(dy / dx);

        double qx = Math.signum(dx);
        double qy = Math.signum(dy);

        if (tan_theta > tan_phi) {
            return new Point2D(
                    B.getX() + (h / tan_theta) * qx,
                    B.getY() + h * qy
            );
        } else {
            return new Point2D(
                    B.getX() + w * qx,
                    B.getY() + w * tan_theta * qy
            );
        }
    }

    void updatePosition(Point2D startPoint) {
        Node arrow;
        if (type == ERelationType.GENERALIZATION && !isEnd) {
            Polygon polygon = new Polygon(0, 0, 0, 16, 16, 0);
            polygon.setFill(Paint.valueOf("white"));
            polygon.setStroke(Paint.valueOf("black"));
            polygon.setStrokeWidth(2);
            arrow = polygon;
        } else if (type == ERelationType.COMPOSITION && !isEnd) {
            arrow = new Rectangle(16, 16, Paint.valueOf("black"));
        } else if (type == ERelationType.AGGREGATION && !isEnd) {
            Rectangle rectangle = new Rectangle(16, 16, Paint.valueOf("white"));
            rectangle.setStroke(Paint.valueOf("black"));
            rectangle.setStrokeWidth(2);
            arrow = rectangle;
        } else {
            return;
        }

        Point2D center = new Point2D(
                node.getTranslateX() + node.getWidth() / 2,
                node.getTranslateY() + node.getHeight() / 2
        );
        Point2D intersect = lineIntersectionOnRect(
                node.getWidth(),
                node.getHeight(),
                center,
                startPoint
        );
        double dx = startPoint.getX() - center.getX();
        double dy = startPoint.getY() - center.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        arrow.setTranslateX(intersect.getX());
        arrow.setTranslateY(intersect.getY());

        Rotate rotate = new Rotate();
        rotate.setAngle((angle - 45));
        rotate.setPivotX(0);
        rotate.setPivotY(0);
        arrow.getTransforms().add(rotate);

        this.getChildren().clear();
        this.getChildren().add(arrow);
    }
}
