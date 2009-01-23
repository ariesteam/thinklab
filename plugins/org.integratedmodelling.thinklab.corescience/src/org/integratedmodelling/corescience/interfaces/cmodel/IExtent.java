/**
 * IExtent.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * An Extent record is produced by a Conceptual Model to describe the extent of the observation
 * it's linked to. Conceptual models can produce IContextAdaptors to mediate between two
 * compatible extents, and are capable of merging extents to produce unions or intersections
 * of extents.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IExtent {

	/**
	 * Return the total number of granules in this extent.
	 * 
	 * @return
	 */
	int getTotalGranularity();
	
	/**
	 * Return the value of the granule indicated.
	 * 
	 * @param granule
	 * @return
	 * @throws ThinklabException 
	 */
	IValue getState(int granule) throws ThinklabException;
	
	/**
	 * Return the value that is the union of all granules, aggregated in the way that makes
	 * sense for the particular conceptual domain.
	 * @return
	 */
	IValue getFullExtentValue();

	/**
	 * An extent must be capable of returning the conceptual model that generated it.
	 * @return
	 */
	ExtentConceptualModel getConceptualModel();

}
