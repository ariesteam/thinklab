package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Simple interface for a wrapper of any state provider that is capable of
 * generating visualizations. The choice of visualization format should be
 * intelligent and require little configuration, using methods in VisualizationFactory. 
 * 
 * Users can give hints to the type and size of the presentation through 
 * constructors and configuration in the derived types, but the implementing classes
 * should work with sensible defaults using just the API.
 * 
 * @author Ferdinando
 *
 */
public interface IVisualization {

	/**
	 * 
	 * @param dataset
	 */
	public void initialize(IObservationContext context) throws ThinklabException;

	/**
	 * Do the magic. The results will depend on the type of visualization. It should allow
	 * multiple calls with no overhead, so make it remember if it was called before and do
	 * nothing if so.
	 * 
	 * @throws ThinklabException
	 */
	public void visualize() throws ThinklabException;
}
