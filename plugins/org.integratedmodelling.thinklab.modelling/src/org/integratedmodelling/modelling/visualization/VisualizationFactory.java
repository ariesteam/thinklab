package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.currency.CurrencyPlugin;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.ColormapChooser;
import org.integratedmodelling.utils.image.ContourPlot;
import org.integratedmodelling.utils.image.ImageUtil;
import org.integratedmodelling.utils.image.processing.ImageProc;

/**
 * Will become a central access point for all visualization operations. For now has just what I need
 * at the moment.
 * 
 * @author Ferdinando
 *
 */
public class VisualizationFactory {

	static public final String COLORMAP_PROPERTY_PREFIX = "thinklab.colormap";
	static public final String GNUPLOT_PATH_PROPERTY = "thinklab.modelling.gnuplot";
	
	static VisualizationFactory _this = new VisualizationFactory();
	ColormapChooser colormapChooser = new ColormapChooser(COLORMAP_PROPERTY_PREFIX);
	
	public static VisualizationFactory get() {
		return _this;
	}
	
	public void loadColormapDefinitions(Properties properties) throws ThinklabException {
		colormapChooser.load(properties);
	}
	
	public ColorMap getColormap(IConcept c, int levels, boolean forceZero) throws ThinklabException {
		return colormapChooser.get(c, levels);
	}
	
	public ColorMap getColormap(IConcept c, int levels, boolean forceZero, ColorMap def) throws ThinklabException {
		ColorMap ret = getColormap(c, levels, forceZero);
		if (ret == null)
			ret = def;
		return ret;
	}
	
	/**
	 * Called when we don't have a set colormap for this observable.
	 * 
	 * @param state
	 * @param nlevels
	 * @return
	 * @throws ThinklabException 
	 */
	public ColorMap getDefaultColormap(IConcept observable, IState state, int nlevels) throws ThinklabException {
		
		String[] categories  = (String[])state.getMetadata(Metadata.CATEGORIES);
		Boolean isBoolean    = (Boolean)state.getMetadata(Metadata.BOOLEAN);
		Boolean zeroIsNodata = (Boolean)state.getMetadata(Metadata.ZERO_IS_NODATA);

		ColorMap ret = null;
		
		if ((isBoolean != null && isBoolean) || nlevels < 3) {
			ret = ColorMap.getColormap("Blues()", nlevels, true);	
		} else {
			ret = categories == null ? 
				(nlevels < 10 ? 
					ColorMap.getColormap("YlOrRd()", nlevels, zeroIsNodata) : 
					ColorMap.jet(nlevels)) : // default for ordinal data
				ColorMap.random(nlevels); // default for categorical data
		}
		
		if (ret == null) {
			ret = ColorMap.jet(nlevels);
		}				
		return ret;
	}

	public String getDataAsText(IState state, RasterGrid space) throws ThinklabException {

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
			String fileOrNull,
			int x, int y, 
			RasterGrid space) throws ThinklabException {

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		double[] data = state.getDataAsDoubles();
		
		int cols = space.getColumns();
		int rows = space.getRows();

		double[][] plotdata = new double[rows][cols];
		IGridMask mask = space.getMask();
		
		if (data != null) {
			
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					double d = data[space.getIndex(row, col)];
					boolean active = mask == null || mask.isActive(space.getIndex(row, col));
					plotdata[rows-row-1][col] = (!active || Double.isNaN(d)) ? 0.0 : d;
				}
			}
		}

		ContourPlot plot = 
			ContourPlot.createPlot(x, y, 
				ImageProc.gaussianSmooth0(plotdata,1.8));
		
		plot.save(fileOrNull);
		
		return fileOrNull;
	}

	public String makeSurfacePlot(IConcept observable, IState state, 
			String fileOrNull,
			int x, int y, 
			RasterGrid space) throws ThinklabException {

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		int[] idata = Metadata.getImageData(state);
		int nlevels = (Integer)state.getMetadata(Metadata.IMAGE_LEVELS);
		Boolean zeroIsNodata = (Boolean)state.getMetadata(Metadata.ZERO_IS_NODATA);
		
		IGridMask mask = space.getMask();
		if (mask != null) {
			for (int i = 0; i < idata.length; i++) {			
				int[] xy = space.getXYCoordinates(i);
				if (!mask.isActive(xy[0], xy[1]))
					idata[i] = 0;
			}
		}
		
		ColorMap cmap =
			getColormap(observable, nlevels, zeroIsNodata, 
				getDefaultColormap(observable, state, nlevels));

		ImageUtil.createImageFile(ImageUtil.upsideDown(idata, space.getColumns()), 
				space.getColumns(), x, y, cmap, fileOrNull);

		state.setMetadata(Metadata.COLORMAP, cmap);
		
		return fileOrNull;
	}
	
	public String makeUncertaintyMask(IConcept observable,  IState state,  String fileOrNull,
			int x, int y, RasterGrid space) throws ThinklabException {
		
		double[] data = (double[]) state.getMetadata(Metadata.UNCERTAINTY);
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
				idata[i] = (int)((1 - data[i])*255.0);
			}
			
			if (i == 0) {
				imin = idata[0];
				imax = idata[0];
			} else {
				if (idata[i] > imax) imax = idata[i];
				if (idata[i] < imin) imin = idata[i];
			}
		}
		
		if ((imax - imin) <= 0)
			// nothing to show
			return null;

		ImageUtil.createImageFile(ImageUtil.upsideDown(idata, space.getColumns()), 
				space.getColumns(), x, y, ColorMap.alphamask(256), fileOrNull);

		return fileOrNull;
	}

	/**
	 * Return a pair with an array of image files and one of descriptions; each file
	 * is a rectangle with the uniform color of a state, and each description describes
	 * the data with that color.
	 * 
	 * Must be called after calling one of the display functions that embed a colormap
	 * in the metadata. If not, an exception is thrown.
	 * 
	 * TODO should return null for continuous, undiscretized data models.
	 * 
	 * @param state
	 * @return
	 * @throws ThinklabException 
	 */
	public Pair<File[], String[]> getLegend(IState state, int totalLength, int height, String fileBaseName) throws ThinklabException {
		
		ColorMap cmap = (ColorMap) state.getMetadata(Metadata.COLORMAP);
		String units  = (String) state.getMetadata(Metadata.UNITS);
		Integer offset = (Integer) state.getMetadata(Metadata.IMAGE_TO_CLASS_OFFSET);
		
		if (units == null)
			units = "";
		
		if (cmap == null) {
			throw new ThinklabValidationException("internal: getLegend called on a state without colormap");
		}
		
		if (Metadata.isContinuous(state)) {
			// for now - we should make one segment and describe the whole range in the text
			return null;
		}
		
		double[] breakpoints = (double[])state.getMetadata(Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS);
		HashMap<IConcept, Integer> ranking = Metadata.getClassMappings(state);

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

	private String getRangeDescription(double[] breakpoints, int i, String units) {

		// breakpoints should have lenght = MaxI+1
		double min = breakpoints[i];
		double max = breakpoints[i+1];
		if (!units.equals(""))
			units = " " + units;
		
		String ret = "";
		
		if (Double.isInfinite(min)) {
			ret = " (< " + max + units + ")";
		}  else if (Double.isInfinite(max)) {
			ret = " (> " + min + units + ")";
		} else {
			ret = " (" + min + " - " + max + units + ")";
		}
		return ret;
	}

	public static String getRangeDescription(IState state) {
		
		String ret = "";
		
		double[] adr = (double[]) state.getMetadata(Metadata.ACTUAL_DATA_RANGE);
		String units = (String) state.getMetadata(Metadata.UNITS);
		
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
		if (state.getObservableClass().is(CurrencyPlugin.MONETARY_VALUE_OBSERVABLE)) {
			nf = NumberFormat.getCurrencyInstance();
		}
		
		Double max = (Double)state.getMetadata(Metadata.AGGREGATED_MAX);
		Double min = (Double)state.getMetadata(Metadata.AGGREGATED_MIN);
		Double tot = (Double)state.getMetadata(Metadata.AGGREGATED_TOTAL);
		String units = (String) state.getMetadata(Metadata.UNITS);
		
		if (tot != null)
			ret += nf.format(tot);

		if (min != null && max != null) {
			ret += (ret.equals("") ? "" : " ") + 
				"[" + nf.format(min) + " - " + nf.format(max) + "]";
		}
		
		if (!ret.equals("") && units != null)
			ret += " " + units;
		
		return ret.equals("") ? null : ret;
		
	}
}
