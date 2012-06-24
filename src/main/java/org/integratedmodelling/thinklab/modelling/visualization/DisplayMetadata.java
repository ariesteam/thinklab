package org.integratedmodelling.thinklab.modelling.visualization;

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
