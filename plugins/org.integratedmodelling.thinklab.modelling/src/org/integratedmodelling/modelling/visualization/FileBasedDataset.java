package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.modelling.Context;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.ImageUtil;

/**
 * 
 * This whole thing must be eliminated and substituted with the proper 
 * context/dataset/visualization logics. Remains here to avoid breaking
 * the ARIES GUI until it's straightened out.
 * 
 * @author Ferdinando
 * @deprecated 
 */
public class FileBasedDataset  {

	private IObservationContext context;
	private GridExtent          space;

	// colormap identifiers
	public static final int GREY = 0;
	public static final int GREEN = 1;
	public static final int RED = 2;
	public static final int BLUE= 3;
	public static final int YELLOW = 4;
	public static final int RAINBOW = 5;
	
	public FileBasedDataset(IObservationContext obs) throws ThinklabException {

		this.context = obs;
		IExtent spc   = Context.getSpace(obs);
		
		if (spc == null || !(spc instanceof GridExtent))
			throw new ThinklabUnimplementedFeatureException(
					"only raster grid data are supported in file exporter for now");
		//time  = (RasterGrid) Obs.findObservation(o, TimePlugin.GridObservable());
		this.space = (GridExtent)spc; 
	}
	
//	@Override
	public ColorMap chooseColormap(IConcept observable, int nlevels, boolean isCategorical) throws ThinklabException {
		
		IState state = getState(observable);
		ColorMap ret = VisualizationFactory.get().getColormap(observable, nlevels, false);

		if (ret == null) {

			ret = (isCategorical && nlevels < 10) ? 
					ColorMap.getColormap("Set1()", nlevels, null) : 
					ColorMap.jet(nlevels);
			
			if (state.getMetadata().get(Metadata.RANKING) != null ||
					state.getMetadata().get(Metadata.CONTINUOUS) != null) {

				// ordered rankings
				
				
			} else if (state.getMetadata().get(Metadata.BOOLEAN) != null && 
					state.getMetadata().get(Metadata.UNCERTAINTY) != null) {
				
					// probability of true - should normalize to 0
					ret = ColorMap.greyscale(nlevels);
					
			} else {
			
			}
		}
		
		return ret;
	}
	
//	@Override
	public void dump(IConcept concept) {
		// TODO Auto-generated method stub

	}

//	@Override
	public void dumpAll() {
		// TODO Auto-generated method stub

	}

//	@Override
	public Collection<IConcept> getObservables() {
		return this.context.getStateObservables();
	}

//	@Override
	public IState getState(IConcept observable) {
		return context.getState(observable);
	}

//	@Override
	public int getStateCount() {
		return context.getStates().size();
	}

//	@Override
	public Collection<IConcept> getStatefulObservables() {
		return getObservables();
	}

//	@Override
	public boolean isSpatial() {
		return space != null;
	}

//	@Override
	public boolean isTemporal() {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
	public String makeContourPlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public String makeHistogramPlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public String makeSurfacePlot(IConcept observable, 
			String fileOrNull,
			int x, int y, 
			int... flags) throws ThinklabException {
		
		IState state = context.getState(observable);

		if (fileOrNull == null) {
			try {
				fileOrNull = File.createTempFile("img", ".png").toString();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		
		int[] idata = Metadata.getImageData(state);
		
		int nlevels = (Integer)state.getMetadata().get(Metadata.IMAGE_LEVELS);
		int[] iarange = (int[])state.getMetadata().get(Metadata.ACTUAL_IMAGE_RANGE);
		double[] dtrange = (double[])state.getMetadata().get(Metadata.THEORETICAL_IMAGE_RANGE);
		double[] darange = (double[])state.getMetadata().get(Metadata.ACTUAL_DATA_RANGE);
		String[] categories = (String[])state.getMetadata().get(Metadata.CATEGORIES);

		System.out.println("metadata: " + state.getMetadata());
		
		System.out.println(observable + ": img [" + iarange[0] + " " + iarange[1] + "] data [" + darange[0] + " " + darange[1] + "]");

		ColorMap cmap = chooseColormap(observable, nlevels, categories == null);
		ImageUtil.createImageFile(ImageUtil.upsideDown(idata, space.getXCells()), 
				space.getXCells(), x, y, cmap, fileOrNull);
		
		return fileOrNull;
	}
	
//	@Override
	public String makeTimeSeriesPlot(IConcept observable, String fileOrNull,
			int x, int y, int... flags) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public String makeUncertaintyMask(IConcept observable, String fileOrNull,
			int x, int y, int... flags) throws ThinklabException {
		
		IState state = context.getState(observable);
		double[] data = (double[]) state.getMetadata().get(Metadata.UNCERTAINTY);
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
		
		System.out.println(observable + ": uncertainty img [" + imin + " " + imax + "]");
		
		if ((imax - imin) <= 0)
			// nothing to show
			return null;

		ImageUtil.createImageFile(ImageUtil.upsideDown(idata, space.getXCells()), 
				space.getXCells(), x, y, ColorMap.alphamask(256), fileOrNull);

		return fileOrNull;
	}

	public GridExtent getGrid() {
		return space;
	}

//	@Override
	public void initialize(IObservation observation) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public void initialize(URL resource) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public void persist(File resource) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
