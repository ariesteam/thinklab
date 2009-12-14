package org.integratedmodelling.geospace.transformations;

import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.geospace.extents.GridExtent;


/**
 * Encodes the details of a rasterization operation for a vector coverage.
 * Rasterization may involve subsetting and warping of each shape. 
 * 
 * @author Ferdinando Villa
 */
public class Rasterize implements IDatasourceTransformation {

	private GridExtent extent = null;

	public Rasterize(GridExtent extent) {
		this.extent  = extent;
	}
	
	public GridExtent getExtent() {
		return this.extent;
	}
	
	@Override
	public String toString() {
		return "rasterize to " + extent;
	}
	
}
