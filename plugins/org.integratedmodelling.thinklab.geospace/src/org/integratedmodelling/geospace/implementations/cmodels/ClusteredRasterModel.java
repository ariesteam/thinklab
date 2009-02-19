package org.integratedmodelling.geospace.implementations.cmodels;

import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A spatial coverage model whose subdivisions are the equal-valued partitions of a main classification
 * raster coverage. The multiplicity is the total number of different values in the classification
 * coverage. Useful to cut out raster datasets based on attribute maps (e.g. land use) or to give
 * semantic sense to clustering algorithms.
 * 
 * @author Ferdinando
 *
 */
public class ClusteredRasterModel extends RegularRasterModel {

	/**
	 * Build the cluster model from the passed coverage; classify the coverage on the
	 * spot.
	 */
	public ClusteredRasterModel(RasterCoverage classification) {
		
		super(classification.getXRangeOffset(), 
			  classification.getXRangeMax(),
			  classification.getYRangeOffset(),
			  classification.getYRangeMax(),
			  classification.getLatLowerBound(),
			  classification.getLatUpperBound(),
			  classification.getLonLowerBound(),
			  classification.getLonUpperBound(),
			  classification.getCoordinateReferenceSystem());
		
	}
	
	/**
	 * Build the cluster model using the passed coverage, containing the passed
	 * classes of values.
	 * 
	 * @param classification
	 * @param classes
	 */
	public ClusteredRasterModel(RasterCoverage classification, int[] classes) {
		// TODO Auto-generated constructor stub
	}


}
