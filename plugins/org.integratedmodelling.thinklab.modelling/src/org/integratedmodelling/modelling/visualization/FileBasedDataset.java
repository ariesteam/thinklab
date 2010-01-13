package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.modelling.interfaces.IDataset;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.ImageUtil;

/**
 * 
 * @author Ferdinando
 *
 */
public class FileBasedDataset implements IDataset {

	private Map<IConcept, IState> states;
	private RasterGrid space;

	// colormap identifiers
	public static final int GREY = 0;
	public static final int GREEN = 1;
	public static final int RED = 2;
	public static final int BLUE= 3;
	public static final int YELLOW = 4;
	public static final int RAINBOW = 5;

	
	public static ColorMap getColormap(int color, int levels) {
		ColorMap ret = null;

		switch (color) {
		case GREY:
			ret = ColorMap.greyscale(levels);
			break;
		case GREEN:
			ret = ColorMap.greenscale(levels);
			break;
		case RED:
			ret = ColorMap.redscale(levels);
			break;
		case BLUE:
			ret = ColorMap.bluescale(levels);
			break;
		case YELLOW:
			ret = ColorMap.yellowscale(levels);
			break;
		case RAINBOW:
			ret = ColorMap.rainbow(levels);
			break;
		}
		
		return ret;
	}
	
	/*
	 * default to greys
	 */
	public int getColorForConcept(IConcept observable) {
		return GREY;
	}
	
	public FileBasedDataset(IObservation obs) throws ThinklabException {

		this.states = ObservationFactory.getStateMap(obs);

		IObservation spc = ObservationFactory.findTopology(obs, Geospace.get().SubdividedSpaceObservable());		
		if (spc == null || !(spc instanceof RasterGrid))
			throw new ThinklabUnimplementedFeatureException(
					"only raster grid data are supported in file exporter for now");

		//time  = (RasterGrid) Obs.findObservation(o, TimePlugin.GridObservable());
		this.space = (RasterGrid)spc; 
	}
	
	@Override
	public ColorMap chooseColormap(
			IConcept observable, double actualMin, double actualMax,
			int minIndex, int maxIndex) {
		
		IState state = getState(observable);
		
		if (state.getMetadata(Metadata.RANKING) != null) {
			
			// ordered rankings
			return getColormap(getColorForConcept(observable), maxIndex - minIndex + 1);
			
		} else if (state.getMetadata(Metadata.BOOLEAN) != null && 
				   state.getMetadata(Metadata.UNCERTAINTY) != null) {
			// probability of true - should normalize to 0
			return ColorMap.greyscale(maxIndex - minIndex + 1);
		}
		
		return ColorMap.rainbow(maxIndex - minIndex + 1);
	}
	
	@Override
	public void dump(IConcept concept) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dumpAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<IConcept> getObservables() {
		return this.states.keySet();
	}

	@Override
	public IState getState(IConcept observable) {
		return states.get(observable);
	}

	@Override
	public int getStateCount() {
		return states.size();
	}

	@Override
	public Collection<IConcept> getStatefulObservables() {
		return getObservables();
	}

	@Override
	public boolean isSpatial() {
		return space != null;
	}

	@Override
	public boolean isTemporal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String makeContourPlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String makeHistogramPlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String makeSurfacePlot(IConcept observable, 
			String fileOrNull,
			int x, int y, 
			int... flags) throws ThinklabException {
		
		IState state = states.get(observable);

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		double[] data = state.getDataAsDoubles();
		int len = data.length;
		int[] idata = new int[len];
		double min = Double.isNaN(data[0]) ? 0 : data[0];
		double max = min;

//		for (int i = 0; i < len; i++) {
//			idata[i] = Double.isNaN(data[i]) ? 0 : (int)data[i];
//		}
		
		for (int i = 0; i < len; i++) {
			if (!Double.isNaN(data[i])) {
				if (data[i] > max) max = data[i];
				if (data[i] < min) min = data[i];
			}
		}

		if (Double.isNaN(min)) min = 0;
		if (Double.isNaN(max)) max = 0;
		
		if (max - min <= 0.0) 
			return null;
		
		int imin = 0, imax = 0;
		for (int i = 0; i < len; i++) {
			
			if (Double.isNaN(data[i]))
				idata[i] = 0;
			else {
				idata[i] = (int)(((data[i]-min)/(max-min))*255.0);
			}
			
			if (i == 0) {
				imin = idata[0];
				imax = idata[0];
			} else {
				if (idata[i] > imax) imax = idata[i];
				if (idata[i] < imin) imin = idata[i];
			}
		}
		
		System.out.println(observable + ": data [" + min + " " + max + "] img [" + imin + " " + imax + "]");
		
		if ((imax - imin) <= 0)
			// nothing to show
			return null;

		ColorMap cmap = chooseColormap(observable, min, max, imin, imax);
		ImageUtil.createImageFile(ImageUtil.upsideDown(idata, space.getColumns()), 
				space.getColumns(), x, y, cmap, fileOrNull);
		
		return fileOrNull;
	}

	@Override
	public String makeTimeSeriesPlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String makeUncertaintyMask(IConcept observable, String fileOrNull,
			int x, int y, int... flags) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
