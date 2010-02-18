package org.integratedmodelling.modelling.visualization;

import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.ColormapChooser;

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
	
}
