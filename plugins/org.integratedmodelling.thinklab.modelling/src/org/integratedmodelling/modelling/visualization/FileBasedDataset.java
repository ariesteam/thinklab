package org.integratedmodelling.modelling.visualization;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
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

	private Map<IConcept, IContextualizedState> states;
	private RasterGrid space;

	public FileBasedDataset(IObservation obs) throws ThinklabException {

		this.states = Obs.getStateMap(obs);
		IObservation spc = Obs.findObservation(obs, Geospace.get().SubdividedSpaceObservable());		
		if (spc == null || !(spc instanceof RasterGrid))
			throw new ThinklabUnimplementedFeatureException(
					"only raster grid data are supported in NetCDF exporter for now");

		//time  = (RasterGrid) Obs.findObservation(o, TimePlugin.GridObservable());
		this.space = (RasterGrid)spc; 
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
	public IContextualizedState getState(IConcept observable) {
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
	public String makeSurfacePlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) throws ThinklabException {
		
		IContextualizedState state = states.get(observable);

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
		double min = data[0];
		double max = data[0];

		for (int i = 0; i < len; i++) {
			idata[i] = (int)data[i];
		}
//		for (int i = 0; i < len; i++) {
//			if (data[i] > max) max = data[i];
//			if (data[i] < min) min = data[i];
//		}
//		
//		System.out.println("min = " + min + " max = " + max);
//		int imin = 0, imax = 0;
//		for (int i = 0; i < len; i++) {
//			idata[i] = (int)(((data[i]-min)/(max-min))*256.0);
//			if (i == 0) {
//				imin = idata[0];
//				imax = idata[0];
//			} else {
//				if (idata[i] > imax) imax = idata[i];
//				if (idata[i] < imin) imin = idata[i];
//			}
//		}
//		System.out.println("imin = " + imin + " imax = " + imax);
		
		// TODO talk about wrong
		ColorMap cmap = new ColorMap(16, new Color[] { Color.WHITE, Color.BLUE,
				Color.RED });
		
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

}
