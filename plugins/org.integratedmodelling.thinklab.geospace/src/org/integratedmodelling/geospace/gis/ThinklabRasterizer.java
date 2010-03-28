package org.integratedmodelling.geospace.gis;

import java.awt.image.RenderedImage;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.feature.FeatureIterator;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.RasterActivationLayer;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.exceptions.ThinklabRasterizationException;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.FeatureRasterizer.FeatureRasterizerException;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
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
	public static RasterCoverage rasterize(VectorCoverage vCoverage, String valueId, float noData, 
			GridExtent extent, IConcept valueType, String valueDefault)  throws ThinklabException  {
		
		if (extent.getCRS() != null)
			vCoverage = (VectorCoverage)vCoverage.requireMatch(extent, false);
		
		GridCoverage2D coverage = null;
		FeatureRasterizer rasterizer = 
			new FeatureRasterizer(
					extent.getYCells(), extent.getXCells(), noData, 
					vCoverage.getAttributeDescriptor(valueId));
		
		if (Geospace.isLongitudeX(vCoverage.getCoordinateReferenceSystem()))
			rasterizer.swapAxes(true);
		
		FeatureIterator<SimpleFeature> iterator = null;
		try {
			
			iterator = vCoverage.getFeatureIterator(extent.getDefaultEnvelope(), valueId);
			
			coverage = rasterizer.rasterize(
					vCoverage.getLayerName() + 
						"_" + 
						(valueId == null ? "" : valueId) + 
						"_raster",
					iterator, 
					valueId,
					valueType,
					valueDefault,
					extent.getDefaultEnvelope(),
					extent.getNormalizedEnvelope());
			
		} catch (FeatureRasterizerException e) {
			throw new ThinklabRasterizationException(e);
		} finally {
			if (iterator != null)
				iterator.close();
		}
		
		RasterCoverage ret = new RasterCoverage(
				vCoverage.getLayerName() + 
				"_" + 
				(valueId == null ? "" : valueId) + 
				"_raster", coverage);
		
		if (rasterizer.isClassification()) {
			ret.setClassMappings(rasterizer.getClassification());
		}
		
		return ret;
	}
	
	private static IGridMask rasterizeShape(ShapeValue shape, GridExtent grid, int value) throws ThinklabRasterizationException {
		
		RasterActivationLayer ret = (RasterActivationLayer) createMask(grid);
		GridCoverage2D coverage = null;
		FeatureRasterizer rasterizer = 
			new FeatureRasterizer(grid.getYCells(), grid.getXCells(), 0.0f, null);
		
		try {						
			coverage = rasterizer.rasterize(shape, grid, value);
			
		} catch (FeatureRasterizerException e) {
			throw new ThinklabRasterizationException(e);
		} 
		
		/*
		 * turn coverage into mask
		 */
		RenderedImage image = coverage.getRenderedImage();
		RandomIter itera = RandomIterFactory.create(image, null);

		for (int i = 0; i < grid.getTotalGranularity(); i++) {
			
			int[] xy = grid.getXYCoordinates(i);
			
			if (itera.getSampleDouble(xy[0], xy[1], 0) > 0.0) {
				ret.activate(xy[0], xy[1]);
			}
		}
		return ret;
	}
	
	public static IGridMask createMask(GridExtent grid) {
		RasterActivationLayer ret = 
			new RasterActivationLayer(grid.getXCells(), grid.getYCells(), false, grid);
		ret.setCRS(grid.getCRS());
		return ret;
	}
	
	public static IGridMask createMask(ShapeValue shape, GridExtent grid) throws ThinklabException {
		return rasterizeShape(shape, grid, 1);
	}
	
	public static IGridMask addToMask(ShapeValue shape, IGridMask mask) throws ThinklabException {
		mask.or(rasterizeShape(shape, mask.getGrid(), 1));
		return mask;
	}

	
}
