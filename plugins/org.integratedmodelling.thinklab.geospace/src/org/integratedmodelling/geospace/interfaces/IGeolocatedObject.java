package org.integratedmodelling.geospace.interfaces;

import org.integratedmodelling.geospace.literals.ShapeValue;

/**
 * All geolocated objects have a shape, a bounding box and a centroid.
 * @author Ferdinando Villa
 *
 */
public interface IGeolocatedObject {

	/**
	 * 
	 * @return
	 */
	public abstract ShapeValue getShape();
	
	/**
	 * 
	 * @return
	 */
	public abstract ShapeValue getBoundingBox();
	
	/**
	 * 
	 * @return
	 */
	public abstract ShapeValue getCentroid();
}
