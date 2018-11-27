package com.pixnotes.objects;

import java.util.List;
import static java.lang.Math.sqrt;
public class IntersectPoint {
    // Coordinates of the point
    public double x;
    public double y;

    public IntersectPoint() {
    }
    // Create a point from coordinates
    public IntersectPoint(double xVal, double yVal) {
        x = xVal;
        y = yVal;
    }
    // Create a point from another Point object
    public IntersectPoint(final IntersectPoint oldPoint) {
        x = oldPoint.x; // Copy x coordinate
        y = oldPoint.y; // Copy y coordinate
    }
    // Move a point
    void move(double xDelta, double yDelta) {
        // Parameter values are increments to the current coordinates
        x += xDelta;
        y += yDelta;
    }
    // Calculate the distance to another point
    double distance(final IntersectPoint aPoint) {
        return sqrt((x - aPoint.x)*(x - aPoint.x) + (y - aPoint.y)*(y - aPoint.y));
    }

    //Returns sqrt(x2 +y2) without intermediate overflow or underflow.
    public double distanceSqrt(IntersectPoint p) {
        return Math.hypot(this.x - p.x, this.y - p.y);
    }

    // Convert a point to a string
    public String toString() {
        return Double.toString(x) + "," + y; // As “x, y”
    }

    //get closest point
    public IntersectPoint getClosestPoint(List<IntersectPoint> pts) {
        double minDistSoFar = Double.MAX_VALUE;
        IntersectPoint rval = null;

        for (IntersectPoint p : pts) {
            if (p.x == this.x && p.y == this.y) {
                continue;
            }

            double pDist = this.distance(p);
            if (pDist < minDistSoFar) {
                minDistSoFar = pDist;
                rval = p;
            }
        }
        return rval;
    }

    @Deprecated
    //get intersection Points
    public IntersectPoint getIntersectionPoint(List<IntersectPoint> listPoints)
    {
    	IntersectPoint minPoint = null;
        for(int i = 0; i<listPoints.size(); i++)
        {
        	IntersectPoint point = listPoints.get(i);
            minPoint = listPoints.get((i+1) % listPoints.size());
            double minXDistance = minPoint.x-point.x;
            double minYDistance = minPoint.y-point.y;
            double minDist = Math.hypot(minXDistance, minYDistance);

            for(int j = 0; j < listPoints.size(); j++)
            {
                if (i == j) {
                    continue;
                }

                IntersectPoint testPt = listPoints.get(j);
                double dist = Math.hypot(point.x - testPt.x, point.y - testPt.y);
                if (dist < minDist)
                {
                    minDist = dist;
                    minPoint = testPt;
                }
            }
        }
        System.out.println("minPoint:"+minPoint.x+"\t"+minPoint.y);
        return minPoint;
    }
}