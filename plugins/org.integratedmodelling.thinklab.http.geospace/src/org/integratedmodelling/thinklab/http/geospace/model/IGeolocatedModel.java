package org.integratedmodelling.thinklab.http.geospace.model;

import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * Any ThinkcapModel adopting this interface can be used directly as the model for any of the view
 * components in org.integratedmodelling.thinkcap.geospace.
 * 
 * @author Ferdinando
 *
 */
public interface IGeolocatedModel {

	/**
	 * Get the ROI selected by the user, possibly with defaults, or null if we haven't chosen one.
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public ShapeValue getRegionOfInterest() throws ThinklabException;

	/**
	 * Set the ROI by adding a shape to the current one
	 * @return true if there was a shape before, or false if we just set a shape for the
	 * first time.
	 */
	public boolean addRegionOfInterest(ShapeValue region) throws ThinklabException;

	/**
	 * Reset the ROI
	 * @return
	 */
	public void resetRegionOfInterest(ShapeValue region) throws ThinklabException;

	/**
	 * Subtract a shape from the current one
	 * @param region
	 * @throws ThinklabException
	 */
	void subtractRegionOfInterest(ShapeValue region) throws ThinklabException;

}
