/*
 * Copyright (c) 2010, Michael Marner (michael@20papercups.net) 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.pixnotes.michaelmarner.gesture;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a single Gesture.
 * 
 * @author Michael Marner (michael@20papercups.net)
 *
 */
public class Gesture {
	
	/**
	 * The list of points this gesture is made of.
	 */
	private List<Point>	points;
	
	/**
	 * The name of this gesture.
	 */
	public String name;
	
	/**
	 * Default constructor.
	 * 
	 * Creates a new Gesture with an empty set of points, and no name.
	 * This should only be used if you plan on adding points to it later.
	 */
	public Gesture() {
		points = new LinkedList<Point>();
		name = "";
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * Creates a deep copy of a Gesture. I'm sure the Java way of doing this
	 * is by overriding clone or something, but I like C++ copy constructors.
	 * 
	 * @param g The gesture to copy.
	 */
	public Gesture(Gesture g) {
		points = new LinkedList<Point>();
		name = g.name;
		
		for (Point p : g.points) {
			this.points.add(new Point(p));
		}
		
	}
	
	/**
	 * Creates an empty gesture with the specified name.
	 * Still need to add points to it though!
	 * 
	 * @param name The name of this gesture.
	 */
	public Gesture(String name) {
		this.name = name;
		points = new LinkedList<Point>();
	}
	
	/**
	 * Returns the length of the gesture's path, in abstract units.
	 *
	 * @return The length of this path.
	 */
	public float getLength() {
		float length = 0;
		
		for (int i=1;i<points.size(); ++i) {
			length += points.get(i).distanceFrom(points.get(i-1));
		}
		return length;
	}
	
	/**
	 * Calculates the centre of this gesture.
	 * @return the centrepoint of the gesture.
	 */
	public Point getCentroid() {
		float x=0;
		float y=0;
		
		for (Point p : points) {
			x += p.x;
			y += p.y;
		}
		return new Point (x / points.size(), y / points.size());
	}
	
	/**
	 * Adds a point to the Gesture.
	 * 
	 * @param p The point to add.
	 */
	public void add(Point p) {
		points.add(p);
	}
	
	/**
	 * Returns the points used in this gesture path.
	 * 
	 * @return The points this gesture is made of.
	 */
	public List<Point> getPoints() {
		return points;
	}
	
	
	public void rotateToZero() {
		Point centroid = this.getCentroid();
		float theta = (float) Math.atan2(centroid.y - this.points.get(0).y, centroid.x - points.get(0).x);
		this.rotateBy(-theta);
	}
	
	public void rotateBy(float theta) {
		Point centroid = this.getCentroid();
		LinkedList<Point> newPoints = new LinkedList<Point>();
		for (Point p : this.points) {
			float x = (float) ((p.x - centroid.x) * Math.cos(theta) - (p.y - centroid.y) * Math.sin(theta) + centroid.x);
			float y = (float) ((p.x - centroid.x) * Math.sin(theta) + (p.y - centroid.y) * Math.cos(theta) + centroid.y);
			newPoints.add(new Point(x,y));
		}
		this.points = newPoints;
	}
	
	public void resample(float fidelity) {
		LinkedList<Point> newPoints = new LinkedList<Point>();
		
		float pathLength = this.getLength() / (fidelity -1);
		float accumulatedDistance = 0;
		
		if(points.size()>0)
		newPoints.add(new Point(points.get(0)));
		
		for (int i=1;i<points.size(); ++i) {
			float d = points.get(i).distanceFrom(points.get(i-1));
			
			if (accumulatedDistance + d >= pathLength) {
				float x = points.get(i-1).x + ((pathLength-accumulatedDistance) / d) * (points.get(i).x - points.get(i-1).x);
				float y = points.get(i-1).y + ((pathLength-accumulatedDistance) / d) * (points.get(i).y - points.get(i-1).y);
				Point p = new Point(x,y);
				newPoints.add(p);
				points.add(i, p);
				accumulatedDistance = 0;
			}
			else {
				accumulatedDistance += d;
			}
		}
		
		this.points = newPoints;
	}
	
	public void translateToOrigin() {
		Point centroid = getCentroid();
		for (Point p : points) {
			p.x = p.x - centroid.x;
			p.y = p.y - centroid.y;
		}
	}
	
	public void scaleToSquare(float size) {
		BoundingBox box = new BoundingBox(this.points);
		
		for (Point p : this.points) {
			p.x = p.x * (size / box.getWidth());
			p.y = p.x * (size / box.getHeight());
		}
	}
	
	public float distance(Gesture b) {
		float distance = 0;
		for (int i=0;i<points.size(); ++i) {
			// DuongHH -- Fix indexOutOfBounds
			if(i <= b.points.size()-1)
			{	
				distance += points.get(i).distanceFrom(b.points.get(i));
			}
		}
		return distance;
	}
}
