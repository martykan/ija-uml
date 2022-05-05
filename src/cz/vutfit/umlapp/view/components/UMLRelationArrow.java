/*
 * File: Arrow.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.uml.ERelationType;
import javafx.geometry.Point2D;
import javafx.scene.layout.VBox;

public class UMLRelationArrow extends ArrowHead {
    VBox node;
    ERelationType type;
    boolean isEnd;

    public UMLRelationArrow(VBox node, Point2D startPoint, ERelationType type, boolean isEnd) {
        super();
        this.node = node;
        this.type = type;
        this.isEnd = isEnd;

        EArrowType arrowType;
        if (type == ERelationType.GENERALIZATION && !isEnd) {
            arrowType = EArrowType.TRIANGLE;
        } else if (type == ERelationType.COMPOSITION && !isEnd) {
            arrowType = EArrowType.SQUARE_FILLED;
        } else if (type == ERelationType.AGGREGATION && !isEnd) {
            arrowType = EArrowType.SQUARE;
        } else if (type == ERelationType.ASSOCIATION && !isEnd) {
            arrowType = EArrowType.BASIC;
        } else {
            return;
        }
        this.setArrowType(arrowType);
        this.updatePosition(startPoint);
        this.show();
    }

    /**
     * Line intersection algorithm
     * Based on https://stackoverflow.c¨om/questions/1585525/how-to-find-the-intersection-point-between-a-line-and-a-rectangle
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
        if (arrow == null) return;
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

        setAngle(angle - 45);
    }
}
