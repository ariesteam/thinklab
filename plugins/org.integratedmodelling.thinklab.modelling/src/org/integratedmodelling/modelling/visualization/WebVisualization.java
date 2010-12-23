package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

/**
 * Just like a FileVisualization, but takes an additional urlPrefix argument and allows
 * to retrieve images as URLs as well as files.
 * 
 * @author Ferdinando
 *
 */
public class WebVisualization extends FileVisualization {

	protected String urlPrefix;

	public WebVisualization(IContext context, String directory, String urlPrefix)
			throws ThinklabException {
		super(context, new File(directory));
		this.urlPrefix = urlPrefix;
	}
	
	public Collection<String> getStateUrls(IConcept c) {
		
		ArrayList<String> ret = new ArrayList<String>();
		for (String s : new String[] {	
				VisualizationFactory.PLOT_SURFACE_2D, VisualizationFactory.PLOT_CONTOUR_2D, 
				VisualizationFactory.PLOT_GEOSURFACE_2D, VisualizationFactory.PLOT_UNCERTAINTYSURFACE_2D, 
				VisualizationFactory.PLOT_GEOCONTOUR_2D, VisualizationFactory.PLOT_TIMESERIES_LINE, 
				VisualizationFactory.PLOT_TIMELAPSE_VIDEO}) {
			
			File f = new File(getStateDirectory(c) + File.separator + s);
			if (f.exists())
				ret.add(urlPrefix + "/" + archive.getStateRelativePath(c) + "/" + s);
		}
		return ret;
	}
	
	/**
	 * Return the state at the given click point in an image produced by this visualization. Assumes 
	 * the image is a spatial map and the states are raster cells; returns null if not.
	 * Transform image coordinates into state coordinates using thinklab/gis conventions.
	 * 
	 * @param imgX
	 * @param imgY
	 * @return
	 */
	public Object getDataAt(IConcept concept, int imgX, int imgY) {
		
		IExtent sp = context.getSpace();
		
		if (!(sp instanceof GridExtent))
			return null;
		
		Object ret = null;
		GridExtent grid = (GridExtent) sp;
		
		double pcx = (double)(getXPlotSize())/(double)(grid.getXCells());
		double pcy = (double)(getYPlotSize())/(double)(grid.getYCells());
		
		int dx = (int)((double)imgX/pcx);
		int dy = (int)((double)(getYPlotSize() - imgY)/pcy);
		
		int idx = grid.getIndex(dx, dy);
			
		ret = context.getState(concept).getValue(idx);
		
		return ret;
	}
	
	/**
	 * Return the URL of the image for the given concept and type, or null if not there.
	 * 
	 * @param c
	 * @param type
	 * @return
	 */
	public String getStateUrl(IConcept c, String type) {
			
		File f = new File(getStateDirectory(c) + File.separator + type);
		if (f.exists())
			return urlPrefix + "/" + archive.getStateRelativePath(c) + "/" + type;
		return null;
	}

	/**
	 * Return the URL of the path containing the concept files.
	 * 
	 * @param c
	 * @return
	 */
	public String getStateUrl(IConcept c) {
		
		File f = getStateDirectory(c);
		if (f.exists())
			return urlPrefix + "/" + archive.getStateRelativePath(c);
		return null;
	}

	public Pair<Double, Double> getGeoCoordinates(int x, int y) {
		
		IExtent sp = context.getSpace();
		
		if (!(sp instanceof GridExtent))
			return null;
		
		GridExtent grid = (GridExtent) sp;
		
		double pcx = (double)(getXPlotSize())/(double)(grid.getXCells());
		double pcy = (double)(getYPlotSize())/(double)(grid.getYCells());
		
		int dx = (int)((double)x/pcx);
		int dy = (int)((double)(getYPlotSize() - y)/pcy);
		
		return new Pair<Double, Double>(
				grid.getWest()  + grid.getEWResolution()*dx + grid.getEWResolution()/2,
				grid.getSouth() + grid.getNSResolution()*dy + grid.getNSResolution()/2);
	}
}
