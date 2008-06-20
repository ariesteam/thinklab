package org.integratedmodelling.geospace.coverage;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.jai.RasterFactory;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GeneralGridRange;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.DefaultProcessor;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.DirectPosition2D;
import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.geospace.GeospacePlugin;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.ThinklabVectorizer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class RasterCoverage implements ICoverage {

	GridCoverage2D coverage = null;
	private CoordinateReferenceSystem crs = null;
	private Envelope boundingBox = null;
	private RenderedImage image = null;
	private GridGeometry2D gridGeometry = null;
//	private RasterActivationLayer activationLayer = null;
	private GridSampleDimension dimension = null;
	private double[] noData = null;
	private String sourceURL;
	private String layerName;
	private double xCellSize;
	private double yCellSize;
	
	static GridCoverageFactory rasterFactory = new GridCoverageFactory();
	
    private static int floatBitsToInt(float f) {
        ByteBuffer conv = ByteBuffer.allocate(4);
        conv.putFloat(0, f);
        return conv.getInt(0);
    }
	
	/**
	 * Produce a new raster coverage from a cell extent and a vector of values that follow the
	 * activation model in the extent. Used after external transformation of spatial data.
	 * @throws ThinklabException 
	 */
	public RasterCoverage(String name, GridExtent extent, Object data) throws ThinklabException {
		
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
		 */
		
		RasterActivationLayer act = extent.requireActivationLayer(true);
		
		if (data instanceof int[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				Pair<Integer, Integer> xy = act.getCell(i);			
				float d = (float) ((int[])data)[i];
				// FIXME check that y-swapping is correct everywhere
				raster.setSample(xy.getFirst(), extent.getYCells() - xy.getSecond() - 1, 0, d);
			}
		} else if (data instanceof long[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				Pair<Integer, Integer> xy = act.getCell(i);			
				// FIXME check that y-swapping is correct everywhere
				raster.setSample(xy.getFirst(), extent.getYCells() - xy.getSecond() - 1, 0, (float) ((long[])data)[i]);
			}
		}  else if (data instanceof float[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				Pair<Integer, Integer> xy = act.getCell(i);			
				// FIXME check that y-swapping is correct everywhere
				raster.setSample(xy.getFirst(), extent.getYCells() - xy.getSecond() - 1, 0, ((float[])data)[i]);
			}
		}  else if (data instanceof double[]) {
			for (int i = 0; i < act.totalActiveCells(); i++) {
				Pair<Integer, Integer> xy = act.getCell(i);			
				// FIXME check that y-swapping is correct everywhere
				raster.setSample(xy.getFirst(), extent.getYCells() - xy.getSecond() - 1, 0, (float)((double[])data)[i]);
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
		
		boundingBox = new Envelope(
				coverage.getEnvelope2D().getMinX(),
				coverage.getEnvelope2D().getMaxX(),
				coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY());
	}
	
	/**
	 * The resampling constructor. Will do its best to produce a new coverage that matches 
	 * a new extent from a different one. Be careful with this one.
	 * 
	 * TODO it should take an interpolation option as a parameter.
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

		// set up the resample operation
		DefaultProcessor processor = new DefaultProcessor(null);
		ParameterValueGroup resampleParams = processor.getOperation("Resample").getParameters();
        resampleParams.parameter("Source").setValue(cov.coverage);
        resampleParams.parameter("CoordinateReferenceSystem").setValue(extent.getCRS());
        resampleParams.parameter("GridGeometry").setValue(this.gridGeometry);

        // resample to create the new coverage
        this.coverage = (GridCoverage2D)processor.doOperation(resampleParams);
        
       // coverage.show();
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
		
		boundingBox = new Envelope(
				coverage.getEnvelope2D().getMinX(),
				coverage.getEnvelope2D().getMaxX(),
				coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY());
		
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
		
		boundingBox = new Envelope(
				coverage.getEnvelope2D().getMinX(),
				coverage.getEnvelope2D().getMaxX(),
				coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY());
		
	}

	public Envelope getBoundingBox() {
		return boundingBox;
	}
	
	public void writeImage(File outfile, String format) throws ThinklabIOException {
		
        try {
			ImageIO.write(coverage.getRenderedImage(), "png", outfile);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}
	
	public void loadData() {
		
		/*
		 * get rid of old image if we had one
		 */
		if (image != null) {
			image = null;
		}
		
		image = coverage.getRenderedImage();
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
		
		
		// System.out.println("asking for " + x + "," + y + " within " + coverage.getEnvelope2D());
	
		double xx =
			boundingBox.getMinX() + 
			(xCellSize * x) +
			(xCellSize/2.0);
		double yy = 
			boundingBox.getMinY() + 
			(yCellSize * y) +
			(yCellSize/2.0);
		
		if (!boundingBox.contains(xx, yy)) {
			System.out.println("FACXKL");
		}
		
		// System.out.println("got " + xx + "," + yy);
	
		return new DirectPosition2D(xx, yy);
	}
	
	public IValue getSubdivisionValue(int subdivisionOrder, IConceptualModel conceptualModel, ArealExtent extent) throws ThinklabValidationException {
		
		/* determine which active x,y we should retrieve for this order */
		Pair<Integer, Integer> xy = ((GridExtent)extent).getActivationLayer().getCell(subdivisionOrder);
		
		Object data = coverage.evaluate(getPosition(xy.getFirst(),xy.getSecond()));
		IValue ret = null;
		
        final int dataType = image.getSampleModel().getDataType();
        
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:   ret = conceptualModel.validateData(((byte[])data)[0]); break;
            case DataBuffer.TYPE_SHORT:  ret = conceptualModel.validateData(((int[])data)[0]); break;
            case DataBuffer.TYPE_USHORT: ret = conceptualModel.validateData(((int[])data)[0]); break;
            case DataBuffer.TYPE_INT:    ret = conceptualModel.validateData(((int[])data)[0]); break;
            case DataBuffer.TYPE_FLOAT:  ret = conceptualModel.validateData(((float[])data)[0]); break;
            case DataBuffer.TYPE_DOUBLE: ret = conceptualModel.validateData(((double[])data)[0]); break;
        }
		
		/* turn it into what the CM wants */
		
		return ret;
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
	
	public String getCoordinateReferenceSystemCode() throws ThinklabValidationException {
		return GeospacePlugin.getCRSIdentifier(crs, false);
	}

	public String getSourceUrl() {
		return sourceURL;
	}

	public int getXRangeMax() {
		return gridGeometry.getGridRange().getUpper(0);
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

//	public RasterActivationLayer getActivationLayer() {
//		return activationLayer;
//	}

	public boolean matchesExtent(GridExtent extent) {
		
		return 
			extent.getEnvelope().equals(boundingBox) &&
			getXCells() == extent.getXCells() &&
			getYCells() == extent.getYCells() &&
			crs.equals(extent.getCRS());
	}
	
	public ICoverage requireMatch(ArealExtent extent, boolean allowClassChange) throws ThinklabException {
		
		System.out.println("Coverage " + getLayerName() + " requested to match " + extent);
		
		if (! (extent instanceof GridExtent)) {
			throw new ThinklabUnimplementedFeatureException("cannot yet match a raster coverage to a non-raster extent");
		}
		
		GridExtent cext = (GridExtent) extent;
		
		if (matchesExtent(cext)) {
			return this;
		}
		
		/* 
		 * This constructor clones our metadata into a new coverage and
		 * resamples our coverage into another that reflects our extent. 
		 */
		return new RasterCoverage(this, cext);
	}

	public VectorCoverage requireCRS(CoordinateReferenceSystem crs)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDataAsDouble(int x, int y) {

		double[] ret =
			coverage.getDataBlock(new GeneralGridRange(new int[] {x, y}, null), (double[])null);
		
		return ret[0];
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
