package org.integratedmodelling.geospace.gis;

import org.geotools.coverage.grid.GridCoverage2D;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.exceptions.ThinklabRasterizationException;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.FeatureRasterizer.FeatureRasterizerException;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public class ThinklabRasterizer {

	
	/**
	 * Convert the passed vector coverage into a raster coverage that adopts the 
	 * passed extent.
	 * 
	 * @param vCoverage
	 * @param extent
	 * @return
	 */
	public static RasterCoverage rasterize(VectorCoverage vCoverage, String valueId, float noData, GridExtent extent)  throws ThinklabException  {
		
		if (extent.getCRS() != null)
			vCoverage = (VectorCoverage)vCoverage.requireMatch(extent, false);
		
		GridCoverage2D coverage = null;
		FeatureRasterizer rasterizer = new FeatureRasterizer(extent.getYCells(), extent.getXCells(), noData);
		
		try {
			
			coverage = rasterizer.rasterize(
					vCoverage.getLayerName()+ "_" + valueId + "_raster",
					vCoverage.getFeatures(), 
					valueId,
					extent.getEnvelope());
			
		} catch (FeatureRasterizerException e) {
			throw new ThinklabRasterizationException(e);
		}
		
		return new RasterCoverage(vCoverage.getLayerName() + "_" + valueId + "_raster", coverage);
	}
	
	
}
