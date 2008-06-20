/**
 * Overlay.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.gis;

import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Template class that computes a set of objects based on the intersections of the shapes of
 * two passed collections of objects. Objects should all be of same type. User should define
 * two functions, one to extract the shape from an object, another to create an object that
 * intersects the attributes of two others whose shapes intersect.
 * 
 * @author Ferdinando Villa
 * @date Feb 13 2007
 * 
 * @param <T> a type from which a shape can be extracted.
 */
public abstract class Overlay<T> {
	
	/**
	 * Extract the corresponding shape from an object. Return null if object does not
	 * have a shape or shape isn't relevant.
	 * 
	 * @param object
	 * @return
	 */
	public abstract Geometry extractShape(T object);
	
	/**
	 * Intersect attributes of objects a and b into (new?) object which will have the
	 * shape passed as third parameter. Return new object which will end up in the
	 * result collection.
	 * 
	 * @param a
	 * @param b
	 * @param result
	 * @return
	 */
	public abstract T intersectAttributes(T a, T b, Geometry intersection);

	
	/**
	 * This one is called when the intersection is not a polygon. The default
	 * behavior is to do nothing, but applications have the option of redefining
	 * it.
	 * 
	 * @param a
	 * @param b
	 * @param intersection
	 * @return
	 */
	public T intersectNonArealAttributes(T a, T b, Geometry intersection) {
		return null;
	}
	
	
	/* 
	 * overlay two sets of objects from which shapes can be extracted. Ignore any result which is not a polygon. Use virtual 
	 * to merge features from shape 1 and 2 into each result intersection.
	 */
	public Collection<T> overlay(Collection<T> geoms1, Collection<T> geoms2) {
		
		ArrayList<T> ret = new ArrayList<T>();
		
		/*
		 * scan first set
		 */
		for (T og1 : geoms1) {
			
			Geometry g1 = extractShape(og1);
			
			if (g1 == null)
				continue;
			
			/*
			 * get bounding box
			 */
			Geometry bbox = g1.getEnvelope();
			
			/* 
			 * intersect all geometries in second set with any points within bbox. 
			 */
			for (T og2 : geoms2) {
				
				Geometry g2 = extractShape(og2);
				
				if (g2 == null)
					continue;
				
				if (bbox.intersects(g2)) {
					
					/* compute intersection of geometries */
					Geometry ints = g1.intersection(g2);
	
					int ng = ints.getNumGeometries();
					
					/* 
					 * foreach intersection resultant, merge attributes and 
					 * add to return set.
					 */
					if (ng == 1 && ints instanceof Polygon) {
						
						/* intersect and add */
						T toAdd = intersectAttributes(og1, og2, ints);
						if (toAdd != null)
							ret.add(toAdd);
						
					} else if (ng > 1) {
						
						for (int gn = 0; gn < ng; gn++) {
							
							Geometry gg = ints.getGeometryN(gn);
							
							if (gg instanceof Polygon) {
								
								/* intersect and add */
								T toAdd = intersectAttributes(og1, og2, gg);
								if (toAdd != null)
									ret.add(toAdd);
								
							} else {

								/* intersection is not 2d */
								T toAdd = intersectNonArealAttributes(og1, og2, gg);
								if (toAdd != null)
									ret.add(toAdd);

							}
						}		
					} else {

						/* intersection is not 2d */
						T toAdd = intersectNonArealAttributes(og1, og2, ints);
						if (toAdd != null)
							ret.add(toAdd);
						
					}
				}
			}
		}
		
		return ret;
	}
	
	
}
