/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
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
