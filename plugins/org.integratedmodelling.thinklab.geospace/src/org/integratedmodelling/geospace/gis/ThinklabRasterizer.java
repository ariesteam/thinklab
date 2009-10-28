package org.integratedmodelling.geospace.gis;

import java.util.Iterator;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.feature.FeatureIterator;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.exceptions.ThinklabRasterizationException;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.FeatureRasterizer.FeatureRasterizerException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.opengis.feature.simple.SimpleFeature;

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
		FeatureIterator<SimpleFeature> iterator = null;
		try {
			
			/*
			 * TODO
			 * if we need an attribute, we must request it in the query, so
			 * pass it below, which will have to validate it,
			 * infer raster type to use and assess need for a lookup table
			 */
			iterator = vCoverage.getFeatureIterator(extent.getDefaultEnvelope());
			
			coverage = rasterizer.rasterize(
					vCoverage.getLayerName() + 
						"_" + 
						(valueId == null ? "" : valueId) + 
						"_raster",
					iterator, 
					valueId,
					extent.getDefaultEnvelope(),
					extent.getNormalizedEnvelope());
			
		} catch (FeatureRasterizerException e) {
			throw new ThinklabRasterizationException(e);
		} finally {
			if (iterator != null)
				iterator.close();
		}
		
		return new RasterCoverage(
				vCoverage.getLayerName() + 
				"_" + 
				(valueId == null ? "" : valueId) + 
				"_raster", coverage);
	}
	
	
}
