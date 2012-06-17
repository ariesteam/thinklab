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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.iterator.RandomIter;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.gis.ThinklabVectorizer;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public abstract class AbstractRasterCoverage implements ICoverage {

	public static final String NODATA_PROPERTY = "raster.nodata";

	protected GridCoverage2D coverage = null;
	protected CoordinateReferenceSystem crs = null;
	// this one is only non-null if the layer was matched to a different extent; in that case we 
	// keep the original data bbox here so we can check if data are outside its bounds. 
	protected ReferencedEnvelope originalBoundingBox = null;
	protected ReferencedEnvelope boundingBox = null;
	protected GridGeometry2D gridGeometry = null;
	protected GridSampleDimension dimension = null;
	protected double[] noData = null;
	protected String sourceURL;
	protected String layerName;
	protected double xCellSize;
	protected double yCellSize;
	protected RandomIter itera;
	protected RenderedImage image = null;
	/*
	 * allows lazy loading of raster image and JAI iterator
	 */
	protected boolean _loaded = false;
	
	public GridCoverage2D getCoverage() {
		return coverage;
	}
	
	/* if this is not null, the raster encodes mappings to these values, with each raster value mapping
	 * to classMappings[value - 1] and 0 representing no data.
	 */
	String[] classMappings = null;
	
	static GridCoverageFactory rasterFactory = new GridCoverageFactory();
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	@Override
	public ReferencedEnvelope getEnvelope() {
		return boundingBox;
	}

	@Override
	public double[] getNodataValue() {
		return this.noData;
	}
	
	public void writeImage(File outfile, String format) throws ThinklabIOException {
		
        try {
			ImageIO.write(coverage.getRenderedImage(), "png", outfile);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public int getXCells() {
		return getXRangeMax() - getXRangeOffset();
	}

	public int getYCells() {
		return getYRangeMax() - getYRangeOffset();
	}

	/**
	 * Check coverage of point within the ORIGINAL data source. Only meaningful for WCS coverages at the moment.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isCovered(int x, int y) {
		
		if (this.originalBoundingBox == null)
			return true;
		
		double xx =
			boundingBox.getMinX() + 
			(xCellSize * x) /*+
			(xCellSize/2.0)*/;
		double yy = 
			boundingBox.getMinY() + 
			(yCellSize * (getYCells() - y - 1)) /*+
			(yCellSize/2.0)*/;
				
		return originalBoundingBox.contains(xx,yy);
	}
	
	/**
	 * Return the total number of cells in the coverage, including nodata ones.
	 * @return
	 */
	public int getTotalCells() {
		return (getXRangeMax() - getXRangeOffset())*(getYRangeMax() - getYRangeOffset());
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.coverage.ICoverage#show()
	 */
	public void show() {
		coverage.show();
	}

	/**
	 * Gets the physical position in current coordinates that corresponds to the center of
	 * the given pixel.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public DirectPosition2D getPosition(int x, int y) {
		
		double xx =
			boundingBox.getMinX() + 
			(xCellSize * x) +
			(xCellSize/2.0);
		double yy = 
			boundingBox.getMinY() + 
			(yCellSize * (getYCells() - y - 1)) +
			(yCellSize/2.0);
	
		return new DirectPosition2D(xx, yy);
	}
	
	public double getDouble(int x, int y) {
		 return itera.getSampleDouble(x, y, 0);
	}
	
	/**
	 * Return the value at the given subdivision, either a double or whatever string our value maps to
	 * if we're classifying.
	 */
	public Object getSubdivisionValue(int subdivisionOrder, ArealExtent extent) throws ThinklabValidationException {
		
		int[] xy = ((GridExtent)extent).getXYCoordinates(subdivisionOrder);

		/**
		 * force to NaN if outside of source data coverage. Geoserver does this wrong, so we need to take
		 * over.
		 */
		if (!isCovered(xy[0],xy[1]))
			return Double.NaN;
		
		if (classMappings == null) {
			
		   Double r = itera.getSampleDouble(xy[0], xy[1], 0);
		   if (Double.isNaN(r))
			   return r;
		   
		   if (this.noData != null) {
			   for (double nd : noData) 
				   if (Double.compare(r, nd) == 0)
					   return Double.NaN;
		   }
		   return r;
		}
		
		int index = itera.getSample(xy[0], xy[1], 0);
		return 
			index == 0 ?
				null : // the "nodata" of categories
				classMappings[index - 1];
	}

	public double getLatLowerBound() {
		return boundingBox.getMinY();
	}

	public double getLatUpperBound() {
		return boundingBox.getMaxY();
	}

	public double getLonLowerBound() {
		return boundingBox.getMinX();
	}

	public double getLonUpperBound() {
		return boundingBox.getMaxX();
	}

	public double getMaxDataValue() {
		return dimension.getMaximumValue();
	}

	public double getMinDataValue() {
		return dimension.getMinimumValue();
	}

	public double getNoDataValue() {
		// TODO check this is OK - it's clearly not
		return noData == null ? Double.NaN : noData[0];
	}
	
	public String getCoordinateReferenceSystemCode() throws ThinklabException {
		return Geospace.getCRSIdentifier(crs, false);
	}

	public String getSourceUrl() {
		return sourceURL;
	}

	public int getXRangeMax() {
		// todo use getEnvelope2D, then who knows
//		gridGeometry.getEnvelope2D().getMaximum(0);
		return gridGeometry.getGridRange2D().getHigh(0) + 1;
	}

	public int getXRangeOffset() {
		return gridGeometry.getGridRange().getLow(0);
	}

	public int getYRangeMax() {
		return gridGeometry.getGridRange().getHigh(1) + 1;
	}

	public int getYRangeOffset() {
		return gridGeometry.getGridRange().getLow(1);
	}

	public String getLayerName() {
		return layerName;
	}

	public boolean matchesExtent(GridExtent extent) {
		
		return 
			extent.getEnvelope().equals(boundingBox) &&
			getXCells() == extent.getXCells() &&
			getYCells() == extent.getYCells() &&
			crs.equals(extent.getCRS());
	}
	

	/**
	 * Vectorize into a vector coverage.
	 * 
	 * @param arealExtent
	 * @return
	 * @throws ThinklabException
	 */
	public ICoverage convertToVector(GridExtent arealExtent) throws ThinklabException {
		return ThinklabVectorizer.vectorize(this, arealExtent);
	}

	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return crs;
	}

	public void write(File f) throws ThinklabException {

		if ( ! (f.toString().endsWith(".tif") || f.toString().endsWith(".tiff"))) {
			throw new ThinklabUnsupportedOperationException(
					"raster coverage: only GeoTIFF format is supported for now");
		}
		
		GeoTiffWriter gtw;
		try {
			gtw = new GeoTiffWriter(f);
	        gtw.write(coverage, null);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}
	
	@Override
	public void setName(String covId) {
		layerName = covId;
	}
	

	public boolean isLoaded() {
		return _loaded;
	}


}
