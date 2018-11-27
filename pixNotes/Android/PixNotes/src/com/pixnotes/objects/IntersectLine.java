package com.pixnotes.objects;

public class IntersectLine {
    IntersectPoint start; // Start point of line
    IntersectPoint end; // End point of line

    // Create a line from two points
    public IntersectLine(final IntersectPoint start, final IntersectPoint end) {
        this.start = new IntersectPoint(start);
        this.end = new IntersectPoint(end);
    }

    // Create a line from two coordinate pairs
    public IntersectLine(double xStart, double yStart, double xEnd, double yEnd) {
        start = new IntersectPoint(xStart, yStart); // Create the start point
        end = new IntersectPoint(xEnd, yEnd); // Create the end point
    }

    // Calculate the length of a line
    double length() {
        return start.distance(end);
    }

    // Convert a line to a string
    public String toString() {
        return "(" + start+ "):(" + end + ")";
    }


    // Case 1: Return a point as the intersection of two lines
    public IntersectPoint intersects(final IntersectLine line1) {
    	IntersectPoint localPoint = new IntersectPoint(0, 0);
        double num = (this.end.y - this.start.y)*(this.start.x - line1.start.x) -(this.end.x - this.start.x)*(this.start.y - line1.start.y);
        double denom = (this.end.y - this.start.y)*(line1.end.x - line1.start.x) -(this.end.x - this.start.x)*(line1.end.y - line1.start.y);
        localPoint.x = line1.start.x + (line1.end.x - line1.start.x)*num/denom;
        localPoint.y = line1.start.y + (line1.end.y - line1.start.y)*num/denom;
        return localPoint;
    }

    // Case 2: Return a point as the intersection of two lines
    public IntersectPoint getIntersect(IntersectLine line1, IntersectLine line2){
        //double slope = (line1.end.y-line1.start.y)/(line1.end.x-line1.start.x);
    	IntersectPoint X = new IntersectPoint(0,0);
       
        double a1 = (line1.end.y-line1.start.y)/(line1.end.x-line1.start.x);
        double b1 = line1.end.y - a1*line1.end.x;
       
        double a2 = (line2.end.y- line2.start.y)/(line2.end.x-line2.start.x);
        double b2 = line2.end.y - a2*line2.end.x;
       
        // Solve equation to find intersection point
        // y = a1x + b1
        // y = a2x + b2
        // a1x + b1 = a2x + b2
        // (a1 - a2)x = b2 - b1
        double x = (b2 - b1)/(a1 - a2);
        double y = a1*x+b1;
       
        X = new IntersectPoint(x,y);
        System.out.println("Point X:"+X .toString());
        return X;
    }
}