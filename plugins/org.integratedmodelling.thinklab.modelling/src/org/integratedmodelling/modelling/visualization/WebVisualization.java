package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Just like a FileVisualization, but takes an additional urlPrefix argument and allows
 * to retrieve images as URLs as well as files.
 * 
 * @author Ferdinando
 *
 */
public class WebVisualization extends FileVisualization {

	private String urlPrefix;

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
}
