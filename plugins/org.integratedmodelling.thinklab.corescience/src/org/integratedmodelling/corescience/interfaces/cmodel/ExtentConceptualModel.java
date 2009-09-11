/**
 * IExtentConceptualModel.java
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

import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * Conceptual models of observations that define observation extents should be capable of
 * creating, merging and mediating extents. This is a different operation than standard 
 * mediation. In order to represent extents, conceptual models must implement ExtentConceptualModel.
 * 
 * @author Ferdinando Villa
 *
 */
public interface ExtentConceptualModel extends IConceptualModel {

	public enum Coverage {
		FULL,
		PARTIAL,
		NONE
	}
	
	/**
	 * Produce the extent that describes the observation we're linked to.
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IExtent getExtent() throws ThinklabException;
	
	
	/**
	 * Return a restriction that can be used to select the extent on the literal target of the
	 * specified property, or null if that is not possible. The restriction should act on a 
	 * known metadata field declared by the plugin.
	 * @param operator TODO
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public abstract Restriction getConstraint(String operator) throws ThinklabException;
	
	/**
	 * The extent mediator manages the relationship between the extent being contextualized and the one that
	 * we represent. It can accumulate smaller extents until the represented one is covered, and mediate
	 * a value based on the extent covered if the value's conceptual model allows it.
	 * 
	 * This should return null if no mediation is necessary and throw an exception if
	 * mediation is impossible. We used mergeExtents before calling this one, so this should normally
	 * not happen.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 * @throws ThinklabContextualizationException
	 */
	public abstract IExtentMediator getExtentMediator(IExtent extent) throws ThinklabException;
	
	/**
	 * Return an extent that can represent both extents passed, directly or through
	 * mediation. Throw an exception if extents are not compatible.
	 * 
	 * @param original
	 * @param other
	 * @param connector
	 * @param constrainExtent if true, we want the "other" extent to constrain the first
	 * @return a new extent, or the original one if no mediation is necessary. Should never
	 * return null.
	 * @throws ThinklabException 
	 */
	public IExtent mergeExtents(IExtent original, IExtent other, LogicalConnector connector, boolean constrainExtent) throws ThinklabException;


}
