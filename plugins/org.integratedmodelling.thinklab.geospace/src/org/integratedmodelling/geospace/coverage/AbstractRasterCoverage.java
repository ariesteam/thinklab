package org.integratedmodelling.geospace.coverage;

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
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.ThinklabVectorizer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Pair;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public abstract class AbstractRasterCoverage implements ICoverage {

	protected GridCoverage2D coverage = null;
	protected CoordinateReferenceSystem crs = null;
	protected BoundingBox boundingBox = null;
	protected GridGeometry2D gridGeometry = null;
	protected GridSampleDimension dimension = null;
	protected double[] noData = null;
	protected String sourceURL;
	protected String layerName;
	protected double xCellSize;
	protected double yCellSize;
	protected RandomIter itera;
	protected RenderedImage image = null;
	
	static GridCoverageFactory rasterFactory = new GridCoverageFactory();
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
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
			(yCellSize * y) +
			(yCellSize/2.0);
	
		return new DirectPosition2D(xx, yy);
	}
	
	public Object getSubdivisionValue(int subdivisionOrder, IConceptualModel conceptualModel, ArealExtent extent) throws ThinklabValidationException {
		
		/* determine which active x,y we should retrieve for this order. Must flip rows to make it match the original image. */
		Pair<Integer, Integer> xy = ((GridExtent)extent).getActivationLayer().getCell(subdivisionOrder);
		return itera.getSampleDouble(xy.getFirst(), getYCells() - xy.getSecond() - 1, 0);
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
		return noData == null ? -9999.0 : noData[0];
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
		return gridGeometry.getGridRange2D().getUpper(0);
	}

	public int getXRangeOffset() {
		return gridGeometry.getGridRange().getLower(0);
	}

	public int getYRangeMax() {
		return gridGeometry.getGridRange().getUpper(1);
	}

	public int getYRangeOffset() {
		return gridGeometry.getGridRange().getLower(1);
	}

	public String getLayerName() {
		return layerName;
	}

	public boolean matchesExtent(GridExtent extent) {
		
		return 
			extent.getNormalizedEnvelope().equals(boundingBox) &&
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
		return new ThinklabVectorizer().vectorize(this, arealExtent);
	}

	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return crs;
	}

	public void write(File f) throws ThinklabException {

		if ( ! (f.toString().endsWith(".tif") || f.toString().endsWith(".tiff"))) {
			throw new ThinklabUnimplementedFeatureException(
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

}
