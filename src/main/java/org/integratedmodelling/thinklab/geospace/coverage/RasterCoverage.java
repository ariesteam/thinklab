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
package org.integratedmodelling.thinklab.geospace.coverage;

import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.RasterFactory;
import javax.media.jai.iterator.RandomIterFactory;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.Operations;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.interfaces.IGridMask;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;

public class RasterCoverage extends AbstractRasterCoverage {

	static GridCoverageFactory rasterFactory = new GridCoverageFactory();
	
	public RasterCoverage(IContext context, IState state) throws ThinklabException {
		
		double[] data = state.getDataAsDoubles();
		IExtent space = context.getSpace();
		
		if (data == null || space == null || !(space instanceof GridExtent)) {
			throw new ThinklabValidationException("cannot create a coverage from a non-spatial state");
		}
		
		buildFromData(state.getObservable().getDirectType().getLocalName(), (GridExtent)space, data);
	}
	
	private void buildFromData(String name, GridExtent extent, Object data)  throws ThinklabException {
		/* 
		 * build a coverage 
		 * 
		 * TODO use a raster of the appropriate type - for now there is apparently a bug in geotools
		 * that makes it work only with float.
		 * */
		WritableRaster raster = 
			RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, extent.getXCells(), extent.getYCells(), 1, null);
		
		/*
		 * TODO raster should be pre-filled with a chosen nodata value
		 * TODO use activation layer
		 */		
		IGridMask act = extent.requireActivationLayer(true);
		
		if (data instanceof int[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				float d = (float) ((int[])data)[i];
				int[] xy = extent.getXYCoordinates(i);
				raster.setSample(xy[0], xy[1], 0, d);
			}
		} else if (data instanceof long[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				int[] xy = extent.getXYCoordinates(i);
				raster.setSample(xy[0], xy[1], 0, (float) ((long[])data)[i]);
			}
		}  else if (data instanceof float[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				int[] xy = extent.getXYCoordinates(i);
				raster.setSample(xy[0], xy[1], 0, ((float[])data)[i]);
			}
		}  else if (data instanceof double[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				int[] xy = extent.getXYCoordinates(i);
				raster.setSample(xy[0], xy[1], 0, (float)((double[])data)[i]);
			}
		} else {
			throw new ThinklabValidationException("cannot create a raster coverage from a " + data.getClass());
		}
		
		this.coverage = rasterFactory.create(name, raster, extent.getEnvelope());
		this.layerName = name;
		this.dimension = (GridSampleDimension)coverage.getSampleDimension(0);
		this.crs = coverage.getCoordinateReferenceSystem2D();
		this.gridGeometry = (GridGeometry2D) coverage.getGridGeometry();
		
		/* no data values */
		noData = dimension.getNoDataValues();

		xCellSize = coverage.getEnvelope2D().getWidth()/(double)getXCells();
		yCellSize = coverage.getEnvelope2D().getHeight()/(double)getYCells();
		
		this.boundingBox = new ReferencedEnvelope(
				coverage.getEnvelope2D().getMinX(),
				coverage.getEnvelope2D().getMaxX(),
				coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY(), crs);

	}
	
	/**
	 * Produce a new raster coverage from a cell extent and a vector of values that follow the
	 * activation model in the extent. Used after external transformation of spatial data.
	 * @throws ThinklabException 
	 */
	public RasterCoverage(String name, GridExtent extent, Object data) throws ThinklabException {
		buildFromData(name, extent, data);
	}
	
	/**
	 * The resampling constructor. Will do its best to produce a new coverage that matches 
	 * a new extent from a different one. Be careful with this one.
	 * 
	 * TODO it should take an interpolation option as a parameter.
	 * FIXME the affine transform for the cropping does weird things and tilts the image on its
	 * side. Plus, reprojection doesn't retain values. Until I figure it out, stick to WCS please.
	 * 
	 * @param cov
	 * @param extent
	 * @throws ThinklabException
	 */
	public RasterCoverage(RasterCoverage cov, GridExtent extent) throws ThinklabException {
		
		this.sourceURL = cov.sourceURL;
		this.dimension = cov.dimension;
		this.boundingBox = extent.getEnvelope();
		this.xCellSize = boundingBox.getWidth()/(double)extent.getXCells();
		this.yCellSize = boundingBox.getHeight()/(double)extent.getYCells();
		this.crs = extent.getCRS();
		
		// here's the geometry we want and the crs for the derived coverage
		this.gridGeometry = new GridGeometry2D(extent.getGridRange(), extent.getEnvelope());

		/*
		 * FIXME passing anything other than null here will result in all values being
		 * zero. Even the original CRS causes that. Until this works, no reprojection
		 * can take place.
		 */
		this.coverage = 
			(GridCoverage2D) 
				Operations.DEFAULT.resample(
						cov.coverage, 
						null, 
						this.gridGeometry, 
						new InterpolationNearest());
		
// this would be a simpler way to handle it if there was no CRS
//		this.coverage = 
//			(GridCoverage2D) 
//				Operations.DEFAULT.resample(
//						cov.coverage, 
//						extent.getEnvelope(), new InterpolationNearest());

		
		// this.coverage.show();
		
	}

	public RasterCoverage(String sourceURL, GridCoverage2D coverage, GridSampleDimension dimension, boolean isSingleBand) {
		
		this.sourceURL = sourceURL;
		
		/* add band fragment ONLY if there is more than one band */
		if (!isSingleBand) {
			this.sourceURL += "#" + Escape.forURL(dimension.toString());
		}
		
		this.coverage= coverage;
		this.dimension = dimension;
		this.crs = coverage.getCoordinateReferenceSystem2D();
		this.gridGeometry = (GridGeometry2D) coverage.getGridGeometry();
		
		/* no data values */
		noData = dimension.getNoDataValues();
		
		/* TODO see if we have to add the band info */
		this.layerName = MiscUtilities.getURLBaseName(sourceURL).toLowerCase();
		
		xCellSize = coverage.getEnvelope2D().getWidth()/(double)getXCells();
		yCellSize = coverage.getEnvelope2D().getHeight()/(double)getYCells();
		
		this.boundingBox = new ReferencedEnvelope(
				coverage.getEnvelope2D().getMinX(),
				coverage.getEnvelope2D().getMaxX(),
				coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY(), crs);
	}
	
	public RasterCoverage(String name, GridCoverage2D raster) {
		
		this.coverage = raster;
		this.layerName = name;
		this.dimension = (GridSampleDimension) raster.getSampleDimension(0);
		
		// this.dimension = dimension;
		this.crs = coverage.getCoordinateReferenceSystem2D();
		this.gridGeometry = (GridGeometry2D) coverage.getGridGeometry();
		
		/* no data values */
		noData = dimension.getNoDataValues();

		xCellSize = coverage.getEnvelope2D().getWidth()/(double)getXCells();
		yCellSize = coverage.getEnvelope2D().getHeight()/(double)getYCells();
		
		this.boundingBox = new ReferencedEnvelope(
				coverage.getEnvelope2D().getMinX(),
				coverage.getEnvelope2D().getMaxX(),
				coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY(), crs);

		// coverage.show();
	}

	@Override
	public void loadData() throws ThinklabException {
		
		/*
		 * get rid of old image if we had one
		 */
		if (image != null) {
			image = null;
		}
		
		image = coverage.getRenderedImage();
		itera = RandomIterFactory.create(image, null);
	}
	

	public ICoverage requireMatch(ArealExtent extent, boolean allowClassChange) throws ThinklabException {
		
		// System.out.println("Coverage " + getLayerName() + " requested to match " + extent);
		
		if (! (extent instanceof GridExtent)) {
			throw new ThinklabUnsupportedOperationException("cannot yet match a raster coverage to a non-raster extent");
		}
		
		GridExtent cext = (GridExtent) extent;
		
		if (matchesExtent(cext)) {
			return this;
		}
		
		/* 
		 * This constructor clones our metadata into a new coverage and
		 * resamples our coverage into another that reflects our extent. 
		 */
		RasterCoverage ret = new RasterCoverage(this, cext);
		ret.setClassMappings(classMappings);
		return ret;
	}

	public void setClassMappings(String[] classification) {
		classMappings = classification;
	}

}
