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
