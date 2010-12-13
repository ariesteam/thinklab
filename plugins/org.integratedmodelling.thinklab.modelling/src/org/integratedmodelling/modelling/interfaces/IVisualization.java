package org.integratedmodelling.modelling.interfaces;

import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Interface for a wrapper of any state provider that is capable of
 * generating visualizations. The choice of visualization format is intelligent, so both the
 * MIME type and the URL of the visualization of each concept must be defined by the
 * implementation. Users can give hints to the type and size of the presentation .
 * 
 * @author Ferdinando
 *
 */
public interface IVisualization {

	/**
	 * 
	 * @param dataset
	 */
	public void initialize(IObservationContext context);

	/**
	 * Create a plot with the appropriate MIME type for the context and the type of visualization
	 * represented. Plot should be done in the passed destination - whatever that means - so
	 * it can be used later. Return the MIME type of the created plot.
	 * 
	 * @param concept the observable whose state in the dataset we want to plot.
	 * @param width x size of plot
	 * @param height y size of plot
	 * @param destination file, URL, directory, or any other destination where the plot will be
	 *        found after graph() has returned successfully.
	 * @param properties use to set any options for the plot, which will depend on the visualization
	 * 	      type. Do not make plot generation conditional on properties - i.e., provide sensible
	 * 		  defaults for everything.
	 * @return
	 */
	public String graph(IConcept concept, int width, int height, String destination, Properties properties)
		throws ThinklabException;
	
}
