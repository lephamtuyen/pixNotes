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

import java.util.List;

/**
 * A simple 2D axis aligned bounding box.
 * 
 * @author Michael Marner (michael@20papercups.net)
 *
 */
public class BoundingBox {

	/**
	 * The minimum coordinate.
	 */
	private Point min;
	
	/**
	 * The maximum coordinates.
	 */
	private Point max;
	
	/**
	 * Creates a bounding box for a set of points.
	 * 
	 * @param points The list of points to enclose.
	 */
	public BoundingBox(List<Point> points) {
		min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
		
		for (Point p : points) {
			if (p.x < min.x)
				min.x = p.x;
			if (p.y < min.y)
				min.y = p.y;
			
			if (p.x > max.x)
				max.x = p.x;
			if (p.y > max.y)
				max.y = p.y;
		}
	}
	
	/**
	 * Returns the width of this bounding box.
	 * 
	 * @return Returns the width of this bounding box.
	 */
	public float getWidth() {
		return max.x - min.x;
	}
	
	/**
	 * Returns the height of this bounding box.
	 * 
	 * @return Returns the height of this bounding box.
	 */
	public float getHeight() {
		return max.y - min.y;
	}
}
