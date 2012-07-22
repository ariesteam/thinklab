package org.integratedmodelling.thinklab.visualization;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;

/**
 * Created by VisualizationFactory for a IState. Will contain all that's necessary to know to properly
 * (and quickly) process a state value into a displayable value (e.g. mapped to colors etc). 
 * 
 * Any options for display should be set in this before use, as metadata fields using the provided
 * key constants. If no corresponding option is found, sensible defaults should be used.
 * 
 * @author Ferd
 *
 */
public class DisplayMetadata extends Metadata {

	double[] ddata = null;
	boolean isUnknown;
	boolean hasUnknowns;
	boolean isReal;
	boolean isBoolean;
	double[] actualRange;
	
	IState state;
	
	public DisplayMetadata(IState state) {
		this.state = state;
	}
	
	public void analyzeData() throws ThinklabException {

		if (ddata != null)
			return;

		ddata = state.getDataAsDoubles();

		hasUnknowns = false;
		boolean isReal = false;

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
			isUnknown = (min != null || max != null);
		}
	}

	/**
	 * Remap double data from a datasource into integers that can be used to
	 * create an image. Set metadata so that the image can be annotated
	 * appropriately. Int values will not be more than 255.
	 * 
	 * Metadata fields that may be set are:
	 * 
	 * DATA_TYPE -> {"DISCRETE_RANKS", "CONTINUOUS", "CATEGORIES",
	 * "BOOLEAN_RANKING", "BOOLEAN_PROBABILITY"} ACTUAL_DATA_RANGE -> [min, max]
	 * (doubles) ACTUAL_IMAGE_RANGE -> [min, max] (integers) DISPLAY_RANGE ->
	 * [min, max] DISPLAY_CATEGORIES -> {category strings} (not there if
	 * continuous or boolean prob) DISPLAY_LEVELS -> Integer (0 - 256)
	 * 
	 * @return integer data or null if there's no chance to remap.
	 * @throws ThinklabException 
	 */
	public int[] getImageData() throws ThinklabException {
		
		analyzeData();
		
		if (ddata == null)
			return null;

		int len = ddata.length;
		int[] idata = new int[len];
//
//		double[] dataRange = Metadata.getDataRange(state);
//
//		/*
//		 * see if we have categories and redefine from there.
//		 */
//		HashMap<IConcept, Integer> ranking = (HashMap<IConcept, Integer>) state
//				.getMetadata().get(RANKING);
//		Boolean hasZeroRanking = (Boolean) state.getMetadata().get(HASZERO);
//		if (hasZeroRanking == null)
//			hasZeroRanking = false;
//		Boolean continuous = (Boolean) state.getMetadata().get(CONTINUOUS);
//		if (continuous == null)
//			continuous = false;
//
//		boolean needZeroInColormap = false;
//
//		int offset = 0;
//
//		int nlevels = 255;
//		if (ranking != null && !continuous) {
//			nlevels = ranking.size();
//			if ((Metadata.hasNoDataValues(state) && !hasZeroRanking))
//				nlevels++;
//
//			if (!hasZeroRanking && !Metadata.hasNoDataValues(state)) {
//				// useful levels will start at 1, so subtract 1 to map to
//				// colormap properly
//				offset = 1;
//			} else {
//				needZeroInColormap = true;
//			}
//		}
//
//		state.getMetadata().put(IMAGE_TO_CLASS_OFFSET, new Integer(offset));
//
//		/*
//		 * compute the display data range in actual values from the semantics
//		 */
//		double expmin =
//		// TODO honor metadata-driven min
//		dataRange == null ? 0.0 : dataRange[0];
//		double expmax =
//		// TODO honor metadata-driven max
//		dataRange == null ? 0.0 : dataRange[1];
//
//		double[] distribution = (double[]) state
//				.getMetadata().get(Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS);
//		if (distribution != null) {
//			expmin = 0;
//			expmax = distribution.length - 1;
//		}
//
//		state.getMetadata().put(THEORETICAL_DATA_RANGE,
//				new double[] { expmin, expmax });
//
//		int imin = 0, imax = 0;
//		for (int i = 0; i < len; i++) {
//
//			if (Double.isNaN(data[i]))
//				idata[i] = 0;
//			else {
//				idata[i] = (ranking != null && !continuous) ? ((int) data[i] - offset)
//						: (int) (((data[i] - expmin) / (expmax - expmin)) * (nlevels - 1));
//			}
//
//			if (i == 0) {
//				imin = idata[0];
//				imax = idata[0];
//			} else {
//				if (idata[i] > imax)
//					imax = idata[i];
//				if (idata[i] < imin)
//					imin = idata[i];
//			}
//		}
//
//		state.getMetadata().put(IMAGE_LEVELS, nlevels);
//		state.getMetadata().put(ZERO_IS_NODATA, new Boolean(needZeroInColormap));
//		state.getMetadata().put(ACTUAL_IMAGE_RANGE, new int[] { imin, imax });
//
		return idata;
	}
	
	/**
	 * Return the number of distinct image levels (colors) given the
	 * maximum range input.
	 * @return
	 */
	public int getLevelCount() {
		return 0;
	}
	
	/**
	 * Get the best colormap for the concept we represent. If the observable
	 * has display metadata, honor them, otherwise use best judgment based on
	 * data type etc.
	 * 
	 * @return
	 */
	public ColorMap getColormap() {
		return null;
	}
	
	/**
	 * Return the displayable version of passed object according to the
	 * visualization options we implement.
	 * 
	 * @param object
	 * @return
	 */
	public Number getDisplayData(Object object) {
		return object instanceof Number ? (Number)object : 0.0;
	}
}
