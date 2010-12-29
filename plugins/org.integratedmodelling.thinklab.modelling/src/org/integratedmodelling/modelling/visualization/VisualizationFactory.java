package org.integratedmodelling.modelling.visualization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.currency.CurrencyPlugin;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.visualization.GeoImageFactory;
import org.integratedmodelling.modelling.Context;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.ColormapChooser;
import org.integratedmodelling.utils.image.ContourPlot;
import org.integratedmodelling.utils.image.ImageUtil;
import org.integratedmodelling.utils.image.processing.ImageProc;

/**
 * Will become a central access point for all visualization operations. For now
 * has just what I need at the moment.
 * 
 * @author Ferdinando
 * 
 */
public class VisualizationFactory {

	static public final String COLORMAP_PROPERTY_PREFIX = "thinklab.colormap";
	static public final String GNUPLOT_PATH_PROPERTY = "thinklab.modelling.gnuplot";

	/**
	 * possible plot types. They also are valid file names for a plot of their
	 * type.
	 */
	static public final String PLOT_SURFACE_2D = "surface-2d.png";
	static public final String PLOT_CONTOUR_2D = "contour-2d.png";
	static public final String PLOT_GEOSURFACE_2D = "geosurface-2d.png";
	static public final String PLOT_UNCERTAINTYSURFACE_2D = "uncertainty-2d.png";
	static public final String PLOT_GEOCONTOUR_2D = "geocontour-2d.png";
	static public final String PLOT_TIMESERIES_LINE = "timeseries-line.png";
	static public final String PLOT_TIMELAPSE_VIDEO = "timelapse-video.mpg";

	private static HashMap<String, String> ddesc;

	static {
		ddesc = new HashMap<String, String>();
		ddesc.put(VisualizationFactory.PLOT_SURFACE_2D, "Surface map");
		ddesc.put(VisualizationFactory.PLOT_GEOSURFACE_2D, "Data over imagery");
		ddesc.put(VisualizationFactory.PLOT_CONTOUR_2D, "Contours");
		ddesc.put(VisualizationFactory.PLOT_GEOCONTOUR_2D,
				"Contours over imagery");
		ddesc.put(VisualizationFactory.PLOT_UNCERTAINTYSURFACE_2D,
				"Uncertainty map");
		ddesc.put(VisualizationFactory.PLOT_TIMESERIES_LINE, "Timeseries plot");
		ddesc.put(VisualizationFactory.PLOT_TIMELAPSE_VIDEO, "Timed animation");
	}

	static VisualizationFactory _this = new VisualizationFactory();
	ColormapChooser colormapChooser = new ColormapChooser(
			COLORMAP_PROPERTY_PREFIX);

	public static VisualizationFactory get() {
		return _this;
	}

	public void loadColormapDefinitions(Properties properties)
			throws ThinklabException {
		colormapChooser.load(properties);
	}

	public ColorMap getColormap(IConcept c, int levels, boolean forceZero)
			throws ThinklabException {
		return colormapChooser.get(c, levels);
	}

	public ColorMap getColormap(IConcept c, int levels, boolean forceZero,
			ColorMap def) throws ThinklabException {
		ColorMap ret = getColormap(c, levels, forceZero);
		if (ret == null)
			ret = def;
		return ret;
	}

	static public String getPlotDescription(String plot) {
		return ddesc.get(plot);
	}

	/**
	 * Called when we don't have a set colormap for this observable.
	 * 
	 * @param state
	 * @param nlevels
	 * @return
	 * @throws ThinklabException
	 */
	public ColorMap getDefaultColormap(IConcept observable, IState state,
			int nlevels) throws ThinklabException {

		String[] categories = (String[]) state.getMetadata().get(
				Metadata.CATEGORIES);
		Boolean isBoolean = (Boolean) state.getMetadata().get(Metadata.BOOLEAN);
		Boolean zeroIsNodata = (Boolean) state.getMetadata().get(
				Metadata.ZERO_IS_NODATA);

		ColorMap ret = null;

		if ((isBoolean != null && isBoolean) || nlevels < 3) {
			ret = ColorMap.getColormap("Blues()", nlevels, true);
		} else {
			ret = categories == null ? (nlevels < 10 ? ColorMap.getColormap(
					"YlOrRd()", nlevels, zeroIsNodata) : ColorMap.jet(nlevels))
					: // default for ordinal data
					ColorMap.random(nlevels); // default for categorical data
		}

		if (ret == null) {
			ret = ColorMap.jet(nlevels);
		}
		return ret;
	}

	public String getDataAsText(IState state, RasterGrid space)
			throws ThinklabException {

		StringBuffer ret = new StringBuffer(1024);

		double[] data = state.getDataAsDoubles();

		int x = space.getColumns();
		int y = space.getRows();

		if (data != null) {

			for (int row = 0; row < y; row++) {
				for (int col = 0; col < x; col++) {
					ret.append(data[space.getIndex(row, col)] + " ");
				}
				ret.append("\n");
			}
		}

		return ret.toString();
	}

	public String makeContourPlot(IConcept observable, IState state,
			String fileOrNull, int x, int y, GridExtent space)
			throws ThinklabException {

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}

		((ContourPlot) makeContourImage(observable, state, x, y, space))
				.save(fileOrNull);

		return fileOrNull;
	}

	public BufferedImage makeContourImage(IConcept observable, IState state,
			int x, int y, GridExtent space) throws ThinklabException {

		double[] data = state.getDataAsDoubles();

		int cols = space.getXCells();
		int rows = space.getYCells();

		double[][] plotdata = new double[rows][cols];
		IGridMask mask = space.getActivationLayer();

		if (data != null) {

			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					double d = data[space.getIndex(col, row)];
					boolean active = mask == null
							|| mask.isActive(space.getIndex(col, row));
					plotdata[rows - row - 1][col] = (!active || Double.isNaN(d)) ? 0.0
							: d;
				}
			}
		}

		return ContourPlot.createPlot(x, y,
				ImageProc.gaussianSmooth0(plotdata, 1.8));
	}

	public String makeSurfacePlot(IConcept observable, IState state,
			String fileOrNull, int x, int y, GridExtent space)
			throws ThinklabException {

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}

		int[] idata = Metadata.getImageData(state);
		int nlevels = (Integer) state.getMetadata().get(Metadata.IMAGE_LEVELS);
		Boolean zeroIsNodata = (Boolean) state.getMetadata().get(
				Metadata.ZERO_IS_NODATA);

		IGridMask mask = space.getActivationLayer();
		if (mask != null) {
			for (int i = 0; i < idata.length; i++) {
				int[] xy = space.getXYCoordinates(i);
				if (!mask.isActive(xy[0], xy[1]))
					idata[i] = 0;
			}
		}

		ColorMap cmap = getColormap(observable, nlevels, zeroIsNodata,
				getDefaultColormap(observable, state, nlevels));

		ImageUtil.createImageFile(
				ImageUtil.upsideDown(idata, space.getXCells()),
				space.getXCells(), x, y, cmap, fileOrNull);

		state.getMetadata().put(Metadata.COLORMAP, cmap);

		return fileOrNull;
	}

	public String makeImagerySurfacePlot(IConcept observable, IState state,
			String fileOrNull, int x, int y, GridExtent space)
			throws ThinklabException {

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}

		int[] idata = Metadata.getImageData(state);
		int nlevels = (Integer) state.getMetadata().get(Metadata.IMAGE_LEVELS);
		Boolean zeroIsNodata = (Boolean) state.getMetadata().get(
				Metadata.ZERO_IS_NODATA);

		IGridMask mask = space.getActivationLayer();
		if (mask != null) {
			for (int i = 0; i < idata.length; i++) {
				int[] xy = space.getXYCoordinates(i);
				if (!mask.isActive(xy[0], xy[1]))
					idata[i] = -1;
			}
		}

		ColorMap cmap = getColormap(observable, nlevels, zeroIsNodata,
				getDefaultColormap(observable, state, nlevels));

		BufferedImage bim = GeoImageFactory.get().getRasterImagery(
				space.getNormalizedEnvelope(), x, y, idata, space.getXCells(),
				cmap);

		ImageUtil.saveImage(bim, fileOrNull);

		state.getMetadata().put(Metadata.COLORMAP, cmap);

		return fileOrNull;
	}

	public String makeImageryContourPlot(IConcept observable, IState state,
			String fileOrNull, int x, int y, GridExtent space)
			throws ThinklabException {

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}

		int[] idata = Metadata.getImageData(state);
		int nlevels = (Integer) state.getMetadata().get(Metadata.IMAGE_LEVELS);
		Boolean zeroIsNodata = (Boolean) state.getMetadata().get(
				Metadata.ZERO_IS_NODATA);

		IGridMask mask = space.getActivationLayer();
		if (mask != null) {
			for (int i = 0; i < idata.length; i++) {
				int[] xy = space.getXYCoordinates(i);
				if (!mask.isActive(xy[0], xy[1]))
					idata[i] = -1;
			}
		}

		ColorMap cmap = getColormap(observable, nlevels, zeroIsNodata,
				getDefaultColormap(observable, state, nlevels));

		BufferedImage bim = makeContourImage(state.getObservableClass(), state,
				x, y, space);

		bim = GeoImageFactory.get().paintOverImagery(
				space.getNormalizedEnvelope(), x, y, bim, space.getXCells(),
				cmap);

		ImageUtil.saveImage(bim, fileOrNull);

		state.getMetadata().put(Metadata.COLORMAP, cmap);

		return fileOrNull;
	}

	public String makeUncertaintyMask(IConcept observable, IState state,
			String fileOrNull, int x, int y, GridExtent space)
			throws ThinklabException {

		double[] data = (double[]) state.getMetadata()
				.get(Metadata.UNCERTAINTY);
		double[] odat = state.getDataAsDoubles();

		if (data == null)
			return null;

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}

		int len = data.length;
		int[] idata = new int[len];

		int imin = 0, imax = 0;
		for (int i = 0; i < len; i++) {

			if (Double.isNaN(data[i]) || Double.isNaN(odat[i]))
				idata[i] = 0;
			else {
				idata[i] = (int) ((1 - data[i]) * 255.0);
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

		if ((imax - imin) <= 0)
			// nothing to show
			return null;

		ImageUtil.createImageFile(
				ImageUtil.upsideDown(idata, space.getXCells()),
				space.getXCells(), x, y, ColorMap.alphamask(256), fileOrNull);

		return fileOrNull;
	}

	/**
	 * Return a pair with an array of image files and one of descriptions; each
	 * file is a rectangle with the uniform color of a state, and each
	 * description describes the data with that color.
	 * 
	 * Must be called after calling one of the display functions that embed a
	 * colormap in the metadata. If not, an exception is thrown.
	 * 
	 * TODO should return null for continuous, undiscretized data models.
	 * 
	 * @param state
	 * @return
	 * @throws ThinklabException
	 */
	public Pair<File[], String[]> 
		getLegend(IState state, int totalLength, int height, String fileBaseName) 
		throws ThinklabException {
		
		ColorMap cmap = (ColorMap) state.getMetadata().get(Metadata.COLORMAP);
		String units  = (String) state.getMetadata().get(Metadata.UNITS);
		Integer offset = (Integer) state.getMetadata().get(Metadata.IMAGE_TO_CLASS_OFFSET);
		
		if (units == null)
			units = "";
		
		if (cmap == null) {
			throw new ThinklabValidationException("internal: getLegend called on a state without colormap");
		}
		
		if (Metadata.isContinuous(state.getMetadata())) {
			
			String ext = MiscUtilities.getFileExtension(fileBaseName);
			String bas = MiscUtilities.getFileBasePath(fileBaseName);
			String fn = bas + "_0." + ext;

			cmap.getColorbar(height, new File(fn));
			return new Pair<File[], String[]>(
					new File[]{new File(fn)},
					new String[]{""}
			);
		}
		
		double[] breakpoints = (double[])state.getMetadata().get(Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS);
		HashMap<IConcept, Integer> ranking = Metadata.getClassMappings(state.getMetadata());

		int nlevels = cmap.getVisibleColorCount();
		int w = totalLength/nlevels;
		
		File[] imgs = cmap.getColorLegend(height, w, fileBaseName);
		String[] descs = new String[imgs.length];
		
		int n = 0;
		for (int i = (cmap.hasTransparentZero() ? 1 : 0); i < cmap.getColorCount(); i++) {
			String desc = "";
			if (ranking != null){

				// can't be that many
				IConcept c = null;
				for (IConcept k : ranking.keySet()) {
					if (ranking.get(k) == (i + offset)) {
						c = k;
						break;
					}
				}
				if (c != null) {
					VisualConcept vc = TypeManager.get().getVisualConcept(c);
					desc = vc.getLabel();
					if (breakpoints != null) {
						desc += 
							getRangeDescription(breakpoints, i - (cmap.hasTransparentZero() ? 1 : 0), units);
					}
				}
			} 
			descs[n++] = desc;
		}
		
		return new Pair<File[], String[]>(imgs, descs);
	}

	public HashMap<IConcept, String> getClassLegend(IState state)
			throws ThinklabException {

		ColorMap cmap = (ColorMap) state.getMetadata().get(Metadata.COLORMAP);
		String units = (String) state.getMetadata().get(Metadata.UNITS);
		Integer offset = (Integer) state.getMetadata().get(
				Metadata.IMAGE_TO_CLASS_OFFSET);

		if (units == null)
			units = "";

		if (cmap == null) {
			throw new ThinklabValidationException(
					"internal: getLegend called on a state without colormap");
		}

		if (Metadata.isContinuous(state.getMetadata())) {
			// for now - we should make one segment and describe the whole range
			// in the text
			return null;
		}

		double[] breakpoints = (double[]) state.getMetadata().get(
				Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS);
		HashMap<IConcept, Integer> ranking = Metadata.getClassMappings(state
				.getMetadata());
		HashMap<IConcept, String> ret = new HashMap<IConcept, String>();

		for (int i = (cmap.hasTransparentZero() ? 1 : 0); i < cmap
				.getColorCount(); i++) {
			String desc = "";
			if (ranking != null) {

				// can't be that many
				IConcept c = null;
				for (IConcept k : ranking.keySet()) {
					if (ranking.get(k) == (i + offset)) {
						c = k;
						break;
					}
				}
				if (c != null) {
					VisualConcept vc = TypeManager.get().getVisualConcept(c);
					desc = vc.getLabel();
					if (breakpoints != null) {
						desc += getRangeDescription(breakpoints,
								i - (cmap.hasTransparentZero() ? 1 : 0), units);
					}
					ret.put(c, desc);
				}
			}
		}

		return ret;
	}

	private String getRangeDescription(double[] breakpoints, int i, String units) {

		// breakpoints should have lenght = MaxI+1
		double min = breakpoints[i];
		double max = breakpoints[i + 1];
		if (!units.equals(""))
			units = " " + units;

		String ret = "";

		if (Double.isInfinite(min)) {
			ret = " (< " + max + units + ")";
		} else if (Double.isInfinite(max)) {
			ret = " (> " + min + units + ")";
		} else {
			ret = " (" + min + " - " + max + units + ")";
		}
		return ret;
	}

	public static String getRangeDescription(IState state) {

		String ret = "";

		double[] adr = (double[]) state.getMetadata().get(
				Metadata.ACTUAL_DATA_RANGE);
		String units = (String) state.getMetadata().get(Metadata.UNITS);

		if (adr != null) {
			ret = adr[0] + " to " + adr[1];
			if (units != null)
				ret += " " + units;
		}
		return ret;

	}

	public static String getAggregatedDescription(IState state) {

		String ret = "";

		NumberFormat nf = NumberFormat.getInstance();
		if (state.getObservableClass().is(
				CurrencyPlugin.MONETARY_VALUE_OBSERVABLE)) {
			nf = NumberFormat.getCurrencyInstance();
		}

		Double max = (Double) state.getMetadata().get(Metadata.AGGREGATED_MAX);
		Double min = (Double) state.getMetadata().get(Metadata.AGGREGATED_MIN);
		Double tot = (Double) state.getMetadata()
				.get(Metadata.AGGREGATED_TOTAL);
		String units = (String) state.getMetadata().get(Metadata.UNITS);

		if (tot != null)
			ret += nf.format(tot);

		if (min != null && max != null) {
			ret += (ret.equals("") ? "" : " ") + "[" + nf.format(min) + " - "
					+ nf.format(max) + "]";
		}

		if (!ret.equals("") && units != null)
			ret += " " + units;

		return ret.equals("") ? null : ret;

	}

	/**
	 * Plot the given state in the given way.
	 * 
	 * Note: the x/y are not necessarily smart and passing a size that does not
	 * match the aspect factor may create errors, not only bad images. Use the
	 * viewport definition functions to ensure they're right (see
	 * FileVisualization).
	 * 
	 * @param state
	 * @param context
	 * @param plotType
	 * @param x
	 * @param y
	 * @param fileOrNull
	 * @throws ThinklabException
	 */
	public void plot(IState state, IContext context, String plotType, int x,
			int y, File fileOrNull) throws ThinklabException {

		if (plotType.equals(PLOT_SURFACE_2D)) {

			makeSurfacePlot(state.getObservableClass(), state,
					fileOrNull.toString(), x, y,
					(GridExtent) Context.getSpace(context));

		} else if (plotType.equals(PLOT_CONTOUR_2D)) {

			makeContourPlot(state.getObservableClass(), state,
					fileOrNull.toString(), x, y,
					(GridExtent) Context.getSpace(context));

		} else if (plotType.equals(PLOT_GEOSURFACE_2D)) {

			makeImagerySurfacePlot(state.getObservableClass(), state,
					fileOrNull.toString(), x, y,
					(GridExtent) Context.getSpace(context));

		} else if (plotType.equals(PLOT_GEOCONTOUR_2D)) {

			makeImageryContourPlot(state.getObservableClass(), state,
					fileOrNull.toString(), x, y,
					(GridExtent) Context.getSpace(context));

		} else if (plotType.equals(PLOT_UNCERTAINTYSURFACE_2D)) {

			makeUncertaintyMask(state.getObservableClass(), state,
					fileOrNull.toString(), x, y,
					(GridExtent) Context.getSpace(context));

		} else if (plotType.equals(PLOT_TIMESERIES_LINE)) {

		} else if (plotType.equals(PLOT_TIMELAPSE_VIDEO)) {

		} else {
			throw new ThinklabInappropriateOperationException(
					"cannot produce a plot of type " + plotType + " for "
							+ state.getObservableClass());
		}

	}

	/**
	 * Return strings to match all possible types of plot for the passed state
	 * and context.
	 * 
	 * @param state
	 * @param context
	 * @return
	 */
	public Collection<String> getPlotTypes(IState state, IContext context) {

		ArrayList<String> ret = new ArrayList<String>();

		IExtent space = Context.getSpace(context);
		IExtent time = Context.getTime(context);

		// FIXME this is unnecessarily costly.
		Metadata.analyzeData(state);

		Boolean isContinuous = (Boolean) state.getMetadata().get(
				Metadata.CONTINUOUS);
		boolean hasUncertainty = state.getMetadata().get(Metadata.UNCERTAINTY) != null;

		if (space != null && time == null) {
			ret.add(PLOT_SURFACE_2D);
			ret.add(PLOT_GEOSURFACE_2D);

			// if (isContinuous) {
			ret.add(PLOT_CONTOUR_2D);
			ret.add(PLOT_GEOCONTOUR_2D);
			// }

			if (hasUncertainty)
				ret.add(PLOT_UNCERTAINTYSURFACE_2D);

		} else if (time != null && space == null && time.getValueCount() > 1) {
			ret.add(PLOT_TIMESERIES_LINE);
		} else if (time != null && space != null) {
			ret.add(PLOT_TIMELAPSE_VIDEO);
		}

		return ret;
	}

	/**
	 * Define the plot size for the given state, context and plot type, ensuring
	 * that the longest edge of the plot is the given pixel size.
	 * 
	 * @param maxEdgeLength
	 * @param state
	 * @param context
	 * @return
	 */
	public Pair<Integer, Integer> getPlotSize(int maxEdgeLength,
			IContext context) {

		int x = maxEdgeLength, y = maxEdgeLength;

		IExtent extent = context.getSpace();

		// very sensiblest defaults
		if (maxEdgeLength == 0) {

			if (extent instanceof GridExtent) {
				x = ((GridExtent) extent).getXCells();
				y = ((GridExtent) extent).getYCells();
			} else {
				x = y = 800;
			}
		}

		if (extent instanceof ArealExtent) {

			double dx = ((ArealExtent) extent).getEWExtent();
			double dy = ((ArealExtent) extent).getNSExtent();

			if (dx > dy) {
				y = (int) ((double) x * (dy / dx));
			} else if (dy > dx) {
				x = (int) ((double) y * (dx / dy));
			}
		}

		return new Pair<Integer, Integer>(x, y);
	}

	/**
	 * Define the plot size for the given state, context and plot type, ensuring
	 * that the resulting plot fits maximally within the given viewport.
	 * 
	 * @param maxWidth
	 * @param maxHeight
	 * @param state
	 * @param context
	 * @return
	 */
	public Pair<Integer, Integer> getPlotSize(int maxWidth, int maxHeight,
			IContext context) {

		int x = maxWidth, y = maxHeight;

		IExtent extent = context.getSpace();

		if (extent instanceof GridExtent) {
			
			int dx = ((GridExtent) extent).getXCells();
			int dy = ((GridExtent) extent).getYCells();
			double image_aspect_ratio = (double)dx / (double)dy;

			// largest side of image must fit within corresponding side of viewport
			if (dx > dy) {
				x = maxWidth;
				y = (int)(((double)x) / image_aspect_ratio);
				if (y > maxHeight) {
					// reduce further
					double fc = (double)maxHeight/(double)y;
					x = (int)((double)x*fc);
					y = (int)((double)y*fc);
				}
			} else {
				y = maxHeight;
				x = (int)(((double)y) * image_aspect_ratio);
				if (x > maxWidth) {
					// reduce further
					double fc = (double)maxWidth/(double)x;
					x = (int)((double)x*fc);
					y = (int)((double)y*fc);
				}
			}
		}

		return new Pair<Integer, Integer>(x, y);
	}

}
