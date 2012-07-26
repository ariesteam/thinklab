package org.integratedmodelling.thinklab.visualization;

import java.awt.image.BufferedImage;
import java.io.File;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.thinklab.api.modelling.IClassification;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IMeasuringObserver;
import org.integratedmodelling.thinklab.api.modelling.IRankingObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.IValuingObserver;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.interfaces.IGridMask;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;
import org.integratedmodelling.thinklab.visualization.geospace.GeoImageFactory;
import org.integratedmodelling.utils.image.ImageUtil;
import org.integratedmodelling.utils.image.processing.ImageProc;

/**
 * Created by the VisualizationFactory for a IState. Will contain all that's necessary to properly
 * process a state into suitable display media (e.g. mapped to colors etc). 
 * 
 * Any options for display should be put() in the adapter before getMediaFile() is called, using the metadata fields 
 * using the provided key constants. If no corresponding option is found, sensible defaults are used.
 * 
 * @author Ferd
 * @see {@link VisualizationFactory.getDisplayAdapter}
 */
public class DisplayAdapter extends Metadata {

	/*
	 * metadata fields that will influence the media files produced
	 */
	public static final String MAP_TYPE = "DisplayAdapter.MAP_TYPE";
	public static final String MAP_BACKGROUND = "DisplayAdapter.MAP_BACKGROUND";
	
	/*
	 * field values for the metadata fields above
	 */
	public static final String RASTER = "DisplayAdapter.RASTER";
	public static final String CONTOUR = "DisplayAdapter.CONTOUR";
	public static final String HISTOGRAM = "DisplayAdapter.HISTOGRAM";
	public static final String SATELLITE_IMAGE = "DisplayAdapter.SATELLITE_IMAGE";
	public static final String NO_BACKGROUND = "DisplayAdapter.NO_BACKGROUND";
	
	double[] ddata = null;
	boolean isUnknown;
	boolean hasUnknowns;
	boolean isReal;
	boolean isBoolean;
	boolean isContinuous;

	double[] actualRange;
	double[] theoreticalRange;
	
	IState state;
	IClassification classification;
	private boolean needsZeroInColormap;
	private int displayDataOffset;
	private int displayLevelsCount;
	private int[] actualImageDataRange;
	private boolean isCategorical;
	
	DisplayAdapter(IState state) {
		this.state = state;
		if (state.getObserver() instanceof IClassifyingObserver) {
			this.classification = ((IClassifyingObserver)(state.getObserver())).getClassification();
		} else 	if (state.getObserver() instanceof IMeasuringObserver) {
			this.classification = ((IMeasuringObserver)(state.getObserver())).getDiscretization();
		} else 	if (state.getObserver() instanceof IRankingObserver) {
			this.classification = ((IRankingObserver)(state.getObserver())).getDiscretization();
		} 
		
		this.isContinuous = 
				state.getObserver() instanceof IMeasuringObserver ||
				(state.getObserver() instanceof IRankingObserver &&
					((IRankingObserver)(state.getObserver())).getType() != IRankingObserver.BINARY_CODING &&
					((IRankingObserver)(state.getObserver())).getType() != IRankingObserver.NUMERIC_ENCODING) ||
				state.getObserver() instanceof IValuingObserver;

		this.isCategorical = 
				this.classification != null &&
				this.classification.isCategorical();

	}
	
	/**
	 * Create and return appropriate media to represent the passed state. This will
	 * create a file or a directory in the passed directory and return the relative
	 * path to it.
	 * 
	 * States that are not distributed in either space or time will return null.
	 * States that are distributed in time and not in space will create a x/y time plot.
	 * States that are distributed in space and not in time will create a map image.
	 * States that are distributed in space and time will create a directory of maps with
	 * filenames ending by _n - where n is the ordinal index of each timestep.
	 * 
	 * Options for image creation can be set in this object before this function is
	 * called.
	 * 
	 * @param directory       directory where to produce the media file. File name is 
	 *                        decided by the implementation.
	 * @param viewportWidth   viewport width for final viewport. Media will fit in it but not necessarily
	 * 	                      exactly (if there is an aspect factor to be respected).
	 * @param viewportHeight viewport height for final viewport. Media will fit in it but not necessarily
	 * 	                      exactly (if there is an aspect factor to be respected).
	 *
	 * @return relative path of created file, or null if it was impossible to create it.
	 */
	public String getMediaFile(File directory, int viewportWidth, int viewportHeight) throws ThinklabException {
		
		boolean isSpatial  = this.state.isSpatiallyDistributed();
		boolean isTemporal = this.state.isTemporallyDistributed();
		
		if (isSpatial && !isTemporal) {
			return makeMap(directory, viewportWidth, viewportHeight);
		} else if (isTemporal && !isSpatial) {
			return makeTimeplot(directory, viewportWidth, viewportHeight);
		} else if (isSpatial && isTemporal) {
			return makeMapTimeseries(directory, viewportWidth, viewportHeight);
		}
		
		return null;
	}
	
	private String getFileBaseName() {
		return 
			this.state.getObservable().getDirectType().toString().
				replace(':', '_').replace('.', '_').toLowerCase();
	}
	
	private String makeMapTimeseries(File directory, int viewportWidth,
			int viewportHeight) {
		// TODO Auto-generated method stub
		return null;
	}

	private String makeTimeplot(File directory, int viewportWidth,
			int viewportHeight) {
		// TODO Auto-generated method stub
		return null;
	}

	private String makeMap(File directory, int viewportWidth, int viewportHeight) throws ThinklabException {
		
		String ret = getFileBaseName() + ".png";
		
		Pair<Integer, Integer> xy = 
				GeoImageFactory.getPlotSize(viewportWidth, viewportHeight, 
						this.state.getContext().getSpace());

		/*
		 * TODO raster should only be the default for grids, and we may not have
		 * a grid.
		 */
		String type = this.getString(MAP_TYPE, RASTER);
		String back = this.getString(MAP_BACKGROUND, NO_BACKGROUND);
	
		analyzeData();
		
		if (this.state.getContext().getSpace() instanceof GridExtent) {

			GridExtent space = (GridExtent) this.state.getContext().getSpace();

			if (type.equals(RASTER)) {
			
				int[] data = getImageData();
				if (data == null)
					return null;

				File ofile = new File(directory + File.separator + ret);
			
				if (back.equals(NO_BACKGROUND)) {
				
					ImageUtil.createImageFile(
							ImageUtil.upsideDown(data, space.getXCells()),
							space.getXCells(), xy.getFirst(), xy.getSecond(), getColormap(), ofile.toString());
				} else if (back.equals(SATELLITE_IMAGE)) {
					
					BufferedImage bim = GeoImageFactory.get().getRasterImagery(
							space.getEnvelope(), xy.getFirst(), xy.getSecond(), data, space.getXCells(),
							getColormap());
					ImageUtil.saveImage(bim, ofile.toString());
				}
			
			} else if (type.equals(CONTOUR)) {
			

				int cols = space.getXCells();
				int rows = space.getYCells();

				double[][] plotdata = new double[rows][cols];
				IGridMask mask = space.getActivationLayer();

				if (ddata != null) {

					for (int row = 0; row < rows; row++) {
						for (int col = 0; col < cols; col++) {
							double d = ddata[space.getIndex(col, row)];
							boolean active = mask == null
									|| mask.isActive(space.getIndex(col, row));
							plotdata[rows - row - 1][col] = 
									(!active || Double.isNaN(d)) ? 0.0 : d;
						}
					}
				}


				File ofile = new File(directory + File.separator + ret);

				if (back.equals(NO_BACKGROUND)) {
			
					ContourPlot.createPlot(xy.getFirst(), xy.getSecond(), ImageProc.gaussianSmooth0(plotdata, 1.8)).
						save(ofile.toString());
					
				} else  if (back.equals(SATELLITE_IMAGE)) {
					
					BufferedImage bim = 
							ContourPlot.createPlot(xy.getFirst(), xy.getSecond(), 
									ImageProc.gaussianSmooth0(plotdata, 1.8));
					
					bim = GeoImageFactory.get().paintOverImagery(
							space.getEnvelope(), xy.getFirst(), xy.getSecond(), bim, space.getXCells(),
							getColormap());

					ImageUtil.saveImage(bim, ofile.toString());
				}
			}
		} else {
			
			throw new ThinklabUnsupportedOperationException(
					"support for visualization of non-grid spatial data is still in the works");
		}
		
		return ret;
	}

	private void analyzeData() throws ThinklabException {
				
		if (ddata != null)
			return;

		this.ddata = state.getDataAsDoubles();

		this.hasUnknowns = false;
		this.isReal = false;

		if (ddata != null) {

			int len = ddata.length;

			/*
			 * compute actual min/max
			 */
			Double min = null;
			Double max = null;

			for (int i = 0; i < len; i++) {
				
				if (!Double.isNaN(ddata[i])) {

					if (min == null) {
						min = ddata[i];
					} else {
						if (ddata[i] < min)
							min = ddata[i];
					}
					if (max == null) {
						max = ddata[i];
					} else {
						if (ddata[i] > max)
							max = ddata[i];
					}

					if (!isReal && (ddata[i] - Math.rint(ddata[i]) != 0))
						isReal = true;

				} else {
					hasUnknowns = true;
				}
			}

			if (min != null && max != null) {
				actualRange = new double[] { min, max };
				isBoolean = (min == 0 && max == 1 && !isReal);
			}
			isUnknown = (min == null && max == null);
		}
		
		this.needsZeroInColormap = false;

		this.displayDataOffset = 0;
		this.displayLevelsCount = 255;
		
		if (classification != null && !isContinuous) {
			
			this.displayLevelsCount = classification.getConceptOrder().size();
			if (hasUnknowns && !classification.hasZeroRank())
				this.displayLevelsCount++;

			if (!classification.hasZeroRank() && hasUnknowns) {
				// useful levels will start at 1, so subtract 1 to map to
				// colormap properly
				this.displayDataOffset = 1;
			} else {
				this.needsZeroInColormap = true;
			}
		}

		/*
		 * compute the display data range in actual values from the semantics
		 */
		double[] dataRange = actualRange == null ? null : new double[] {actualRange[0], actualRange[1]};
		double expmin =
		// TODO honor metadata-driven min
		dataRange == null ? 0.0 : dataRange[0];
		double expmax =
		// TODO honor metadata-driven max
		dataRange == null ? 0.0 : dataRange[1];

		double[] distribution = null;
		if (classification != null)
			distribution = classification.getDistributionBreakpoints();
		
		if (distribution != null) {
			expmin = 0;
			expmax = distribution.length - 1;
		}

		this.theoreticalRange = new double[] { expmin, expmax };

	}

	/*
	 * Remap double data from a datasource into integers that can be used to
	 * create an image. Set metadata so that the image can be annotated
	 * appropriately. Int values will not be more than 255.
	 * 
	 * Sets the actual data range for the image into actualImageDataRange as a side effect.
	 * 
	 * @return integer data or null if there's no chance to remap.
	 * @throws ThinklabException 
	 */
	private int[] getImageData() throws ThinklabException {
		
		analyzeData();
		
		if (ddata == null)
			return null;

		int len = ddata.length;
		int[] idata = new int[len];

		/*
		 * we have no data at all, everything is 0
		 */
		if (this.isUnknown)
			return idata;

		/*
		 * if the theoretical range is 0 but the values are not, we only have
		 * one non-zero or non-unknown value. We must account for that.
		 */
		boolean singleValue = 
				this.theoreticalRange[0] == this.theoreticalRange[1] && this.theoreticalRange[0] != 0;
		
		int imin = 0, imax = 0;
		for (int i = 0; i < len; i++) {

			if (Double.isNaN(ddata[i]))
				idata[i] = 0;
			else {
				idata[i] = 
					singleValue ? 
					(Double.isNaN(ddata[i]) || ddata[i] == 0 ? 0 : 1) :
					((classification != null && !isContinuous) ? 
						((int) ddata[i] - this.displayDataOffset) : 
						(int) (((ddata[i] - this.theoreticalRange[0]) / 
									(this.theoreticalRange[1] - this.theoreticalRange[0])) * 
									(this.displayLevelsCount - 1)));
			}

			if (i == 0) {
				imin = idata[0];
				imax = idata[0];
			} else {
				if (idata[i] > imax)
					imax = idata[i];
				if (idata[i] < imin)
					imin = idata[i];
			}
		}

		this.actualImageDataRange = new int[] { imin, imax };

		return idata;
	}
		
	/**
	 * Get the best colormap for the concept we represent. If the observable
	 * has display metadata, honor them, otherwise use best judgment based on
	 * data type etc.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	private ColorMap getColormap() throws ThinklabException {
		
		analyzeData();
		
		ColorMap ret = null;

		/*
		 * TODO override with specified metadata - must find the best way to search
		 * model, concept and provenance records from the state.
		 */
		
		if (isBoolean || displayLevelsCount < 3) {
			ret = ColorMap.getColormap("Blues()", displayLevelsCount, true);
		} else {
			ret = isCategorical ? 
					ColorMap.random(displayLevelsCount) :
					(displayLevelsCount < 10 ? 
						ColorMap.getColormap("YlOrRd()", displayLevelsCount, hasUnknowns) :
						ColorMap.jet(displayLevelsCount));
		}

		if (ret == null) {
			ret = ColorMap.jet(displayLevelsCount);
		}
		return ret;
	}
	
	/**
	 * Return the displayable version of passed object according to the
	 * visualization options we implement.
	 * 
	 * @param object
	 * @return
	 */
	protected Number getDisplayData(Object object) {
		return object instanceof Number ? (Number)object : 0.0;
	}
}
