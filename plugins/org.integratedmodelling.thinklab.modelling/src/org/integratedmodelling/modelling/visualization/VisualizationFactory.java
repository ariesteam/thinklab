package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.ColormapChooser;
import org.integratedmodelling.utils.image.ImageUtil;

/**
 * Will become a central access point for all visualization operations. For now has just what I need
 * at the moment.
 * 
 * @author Ferdinando
 *
 */
public class VisualizationFactory {

	static public final String COLORMAP_PROPERTY_PREFIX = "thinklab.colormap";
	
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
		
		String[] categories = (String[])state.getMetadata(Metadata.CATEGORIES);
		Boolean isBoolean   = (Boolean)state.getMetadata(Metadata.BOOLEAN);
		
		ColorMap ret = null;
		
		if ((isBoolean != null && isBoolean) || nlevels < 3) {
			ret = ColorMap.getColormap("Blues()", nlevels, true);	
		} else {
			ret = categories == null ? 
				(nlevels < 10 ? 
					ColorMap.getColormap("YlOrRd()", nlevels, Metadata.hasZeroCategory(state) || Metadata.hasNoDataValues(state)) : 
					ColorMap.jet(nlevels)) : // default for ordinal data
				ColorMap.random(nlevels); // default for categorical data
		}
		
		if (ret == null) {
			
			/*
			 * TODO ripristinate
			 */
			ret = ColorMap.jet(nlevels);
//			throw new ThinklabResourceNotFoundException(
//					"cannot determine color table for " + 
//					observable + 
//					"; please add colormap entry");
		}				
		return ret;
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
		
		ColorMap cmap =
			getColormap(observable, nlevels, 
				Metadata.hasZeroCategory(state) || Metadata.hasNoDataValues(state), 
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
					if (ranking.get(k) == i) {
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
		
		Double max = (Double)state.getMetadata(Metadata.AGGREGATED_MAX);
		Double min = (Double)state.getMetadata(Metadata.AGGREGATED_MIN);
		Double tot = (Double)state.getMetadata(Metadata.AGGREGATED_TOTAL);
		String units = (String) state.getMetadata(Metadata.UNITS);
		
		if (tot != null)
			ret += "Aggregated total: " + tot;

		if (min != null && max != null) {
			ret += (ret.equals("") ? "" : " ") + "Range min: " + min + " max: " + max;
		}
		
		if (!ret.equals("") && units != null)
			ret += " " + units;
		
		return ret.equals("") ? null : ret;
		
	}
}
