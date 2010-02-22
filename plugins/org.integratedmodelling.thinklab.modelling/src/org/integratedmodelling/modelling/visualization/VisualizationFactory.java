package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
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
	
	public ColorMap getColormap(IConcept c, int levels) throws ThinklabException {
		return colormapChooser.get(c, levels);
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
		int[] iarange = (int[])state.getMetadata(Metadata.ACTUAL_IMAGE_RANGE);
		double[] dtrange = (double[])state.getMetadata(Metadata.THEORETICAL_IMAGE_RANGE);
		double[] darange = (double[])state.getMetadata(Metadata.ACTUAL_DATA_RANGE);
		String[] categories = (String[])state.getMetadata(Metadata.CATEGORIES);

		System.out.println("metadata: " + state.getMetadata());
		System.out.println(observable + ": img [" + iarange[0] + " " + iarange[1] + "] data [" + darange[0] + " " + darange[1] + "]");

		ColorMap cmap = getColormap(observable, nlevels);
		ImageUtil.createImageFile(ImageUtil.upsideDown(idata, space.getColumns()), 
				space.getColumns(), x, y, cmap, fileOrNull);
		
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

	
}
