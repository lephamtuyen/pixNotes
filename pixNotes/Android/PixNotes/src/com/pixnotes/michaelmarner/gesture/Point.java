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

/**
 * An extension to Android's own PointF class, 
 * we are just providing a few methods that make things easier to work with.
 * 
 * @author Michael Marner (michael@20papercups.net)
 *
 */
public class Point extends android.graphics.PointF {

	/**
	 * Copy constructor.
	 * 
	 * Do we normally do copy constructors in Java?
	 * 
	 * @param p The Point to copy.
	 */
	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	/**
	 * Creates a Point with the specified values.
	 * 
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 */
	public Point(float x, float y){
		super(x,y);
	}
	
	/**
	 * Calculates the distance between this and another point.
	 * 
	 * @param p The other point.
	 * @return The distance between two Points.
	 */
	public float distanceFrom(Point p) {
		float deltaX = this.x - p.x;
		float deltaY = this.y - p.y;
		return (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
}
