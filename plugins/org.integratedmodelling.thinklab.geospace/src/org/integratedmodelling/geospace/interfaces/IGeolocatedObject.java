/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.geospace.interfaces;

import org.integratedmodelling.geospace.literals.ShapeValue;

/**
 * All geolocated objects have a shape, a bounding box and a centroid. Because this
 * interface provides a reference location for objects, they are required to return
 * values in WGS84 projection (with the N-S axis corresponding to latitude) independent
 * on the original projection of the object.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IGeolocatedObject {

	/**
	 * Return the overall shape in straight WGS84 with lat on N-S axis.
	 * @return
	 */
	public abstract ShapeValue getShape();
	
	/**
	 * Return the bounding box in straight WGS84 with lat on N-S axis.
	 * @return
	 */
	public abstract ShapeValue getBoundingBox();
	
	/**
	 * Return the centroid in straight WGS84 with lat on N-S axis.
	 * @return
	 */
	public abstract ShapeValue getCentroid();
}
