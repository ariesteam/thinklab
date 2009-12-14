package org.integratedmodelling.geospace.transformations;

import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.geospace.extents.GridExtent;


/**
 * Encodes the details of a resampling operation for a spatial grid.
 * Resampling can be one or more of subsetting, interpolating and warping. 
 * 
 * @author Ferdinando Villa
 */
public class Resample implements IDatasourceTransformation {

	private GridExtent extent = null;

	public Resample(GridExtent extent) {
		this.extent  = extent;
	}
	
	public GridExtent getExtent() {
		return this.extent;
	}
	
	@Override
	public String toString() {
		return "resample to " + extent;
	}

}
