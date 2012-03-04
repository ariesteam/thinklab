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
package org.integratedmodelling.geospace.gis;

import java.awt.image.RenderedImage;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.coverage.RasterActivationLayer;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

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
			GridExtent extent, IConcept valueType, String valueDefault, String valueExpression)  
		throws ThinklabException  {
		
		
		if (extent.getCRS() != null)
			vCoverage = (VectorCoverage)vCoverage.requireMatch(extent, false);
		
		GridCoverage2D coverage = null;
		FeatureRasterizer rasterizer = 
			new FeatureRasterizer(
					extent.getYCells(), extent.getXCells(), noData, 
					vCoverage.getAttributeDescriptor(valueId), extent);
		
		FeatureIterator<SimpleFeature> iterator = null;
		try {
			
			/*
			 * determine the extent of the data, so we can set to no-data for areas outside (and this will
			 * not mask any composed results) and 0 for no-polygons inside the data range. I can't believe it takes 
			 * all this code to ensure a CRS match.
			 */
			BoundingBox bb = vCoverage.getBoundingBox();	
			Coordinate p1 = new Coordinate(bb.getMinX(), bb.getMinY());
			Coordinate p2 = new Coordinate(bb.getMaxX(), bb.getMaxY());
			ReferencedEnvelope dataEnvelope = null;

			try {

				MathTransform transf = CRS.findMathTransform(
						vCoverage.getCoordinateReferenceSystem(), 
						extent.getDefaultEnvelope().getCoordinateReferenceSystem());
				
				CoordinateReferenceSystem crs = extent.getDefaultEnvelope().getCoordinateReferenceSystem();
				
				p1 = JTS.transform(p1,null,transf); 
				p2 = JTS.transform(p2,null,transf); 

				if (crs.getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH)) {
						dataEnvelope = new ReferencedEnvelope(p1.y, p2.y, p1.x, p2.x, crs);
				} else { 
					dataEnvelope = new ReferencedEnvelope(p1.x, p2.x, p1.y, p2.y, crs);
				}
			
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}			
			// OK, done
			
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
					valueExpression,
					extent.getDefaultEnvelope(),
					extent.getNormalizedEnvelope(),
					dataEnvelope);
			
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
	
	private static IGridMask rasterizeShape(ShapeValue shape, GridExtent grid, int value) throws ThinklabException {
		
		RasterActivationLayer ret = (RasterActivationLayer) createMask(grid);
		GridCoverage2D coverage = null;
		FeatureRasterizer rasterizer = 
			new FeatureRasterizer(grid.getYCells(), grid.getXCells(), 0.0f, null, null);
		
		coverage = rasterizer.rasterize(shape, grid, value);
		
		/*
		 * turn coverage into mask
		 */
		RenderedImage image = coverage.getRenderedImage();
		RandomIter itera = RandomIterFactory.create(image, null);

		for (int i = 0; i < grid.getValueCount(); i++) {
			
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
