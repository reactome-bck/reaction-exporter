package org.reactome.server.tools.reaction.exporter.layout.common;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CoordinateUtils {

    public static Point2D intersection(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        return intersection(a1.getX(), a1.getY(), a2.getX(), a2.getY(), b1.getX(), b1.getY(), b2.getX(), b2.getY());
    }

    public static Point2D intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        // calculate the direction of the lines
        double uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
        double uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));

        // if uA and uB are between 0-1, lines are colliding
        if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
            double intersectionX = x1 + (uA * (x2 - x1));
            double intersectionY = y1 + (uA * (y2 - y1));
            return new Point2D.Double(intersectionX, intersectionY);
        }
        return null;
    }

    public static boolean intersects(Line2D line, Rectangle2D rectangle) {
        return intersects(line, new Line2D.Double(rectangle.getMinX(), rectangle.getMinY(), rectangle.getMinX(), rectangle.getMaxY()))
                || intersects(line, new Line2D.Double(rectangle.getMaxX(), rectangle.getMinY(), rectangle.getMaxX(), rectangle.getMaxY()))
                || intersects(line, new Line2D.Double(rectangle.getMinX(), rectangle.getMinY(), rectangle.getMaxX(), rectangle.getMinY()))
                || intersects(line, new Line2D.Double(rectangle.getMaxX(), rectangle.getMinY(), rectangle.getMaxX(), rectangle.getMaxY()));

    }

    public static boolean intersects(Line2D a, Line2D b) {
        return intersects(a.getP1(), a.getP2(), b.getP1(), b.getP2());
    }

    public static boolean intersects(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        return intersects(a1.getX(), a1.getY(), a2.getX(), a2.getY(), b1.getX(), b1.getY(), b2.getX(), b2.getY());
    }

    public static boolean intersects(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        // calculate the direction of the lines
        final double div = (y4 - y3) * (x2 - x1) - (y2 - y1) * (x4 - x3);

        final double numT = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        final double numU = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

        if (div >= -0.0 && div <= 0.0) {
            // segments are parallel
            if (numT >= -0.0 && numT <= 0.0) {
                // segments are in the same line

                // http://geomalgorithms.com/a05-_intersect-1.html
                // In this case, solve for t0 and t1 such that P0 = Q(t0) and P1 = Q(t1).
                // If the segment intervals [t0,t1] and [0,1] are disjoint, there is no intersection.
                // Otherwise, intersect the intervals (using max and min operations) to get [r0,r1] = [t0,t1] ∩ [0,1].
                // Then the intersection segment is Q(r0)Q(r1) = P0P1 ∩ Q0Q1.
                // This works in any dimension.

                // u0 = find x1 or y1 in u
                // u1 = find x2 or y2 in u
                double u0 = (x1 - x3) / (x4 - x3);
                if (Double.isNaN(u0))
                    u0 = (y1 - y3) / (y4 - y3);
                double u1 = (x2 - x3) / (x4 - x3);
                if (Double.isNaN(u1))
                    u1 = (y2 - y3) / (y4 - y3);
                // sort u0, u1
                if (u0 > u1) {
                    double aux = u1;
                    u1 = u0;
                    u0 = aux;
                }
                // interval intersection
                final double min = Math.max(0, u0);
                final double max = Math.min(1, u1);
                return max > min;

            } else {
                // parallel, different lines
//				System.out.println("no intersection (parallel in different lines)");
                return false;
            }
        }

        // Non parallel lines, maths will work
        double t = numT / div;
        double u = numU / div;

        return !(t <= 0) && !(t >= 1) && !(u <= 0) && !(u >= 1);
    }
}
