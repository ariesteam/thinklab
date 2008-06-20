/**
 * IExtentMediator.java
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
package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.corescience.exceptions.ThinklabMediationException;
import org.integratedmodelling.thinklab.interfaces.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Pair;
import org.jscience.mathematics.number.Rational;


/**
 * An extent mediator is a value mediator that has the ability of accumulating foreign extent values
 * and decide when
 * the extent it represents is covered. When asked, it can mediate a value based on the proportion of 
 * coverage of the extent it represents, and reset itself to only cover whatever par of the extent
 * represented could not be used in the mediation.
 * 
 * Note: the mediator functions inherited from IValueMediator need to perform the reset as explained above.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IExtentMediator {

	
	/**
	 * Add the passed foreign extent to the one we represent.
	 * @param extent
	 * @throws ThinklabMediationException 
	 */
	public abstract void addForeignExtent(IValue extent) throws ThinklabMediationException;
	
	/**
	 * Return true if the managed extent is fully covered (possibly with excess) in the state we are in, or false if it is only 
	 * partially covered. Note that entirely uncovered should be an internal error, because we only 
	 * contextualize over the minimum common extents.
	 * 
	 * @return
	 */
	public abstract ExtentCoverage checkCoverage();
	
	/**
	 * Return the current coverage of the extent represented based on the foreign extents added. Use a 
	 * rational number for precision. If any uncertainty is involved in the redistribution of the
	 * original extent over the current coverage, it must return it, after compounding it with the passed 
	 * uncertainty if any was passed.
	 * 
	 * @return
	 */
	public abstract Pair<Rational, IUncertainty> getCurrentCoverage(IUncertainty uncertainty);
	
	/**
	 * Reset the coverage of the foreign extents to whatever is left after subtracting the fully covered
	 * represented extent. Only called just after isCovered() has returned true.
	 *
	 */
	public abstract void resetForeignExtents();
}
