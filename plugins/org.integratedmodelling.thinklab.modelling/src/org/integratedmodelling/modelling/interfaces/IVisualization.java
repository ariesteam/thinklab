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
package org.integratedmodelling.modelling.interfaces;

import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

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
	
	public static final String VIEWPORT_X_PROPERTY = "viewport-x";
	public static final String VIEWPORT_Y_PROPERTY = "viewport-y";

	/**
	 * Visualizations must have an empty constructor. The context passed
	 * should define entirely, and it must be capable of redefining an already
	 * defined visualization from scratch. The properties may contain data 
	 * and attributes coming from configuration; according to the purposes,
	 * visualizations should honor the properties but not depend on them.
	 * 
	 * @param dataset
	 */
	public void initialize(IContext context, Properties properties) throws ThinklabException;

	/**
	 * Do the magic. The results will depend on the type of visualization. It should allow
	 * multiple calls with no overhead, so make it remember if it was called before and do
	 * nothing if so.
	 * 
	 * @throws ThinklabException
	 */
	public void visualize() throws ThinklabException;

	/**
	 * Return the main observable of the visualization (normally the observable
	 * of the top observation in the associated context).
	 * 
	 * @return
	 */
	public IConcept getObservableClass();
}
