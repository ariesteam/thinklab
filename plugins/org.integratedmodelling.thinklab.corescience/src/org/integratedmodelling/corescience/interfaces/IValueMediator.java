/**
 * IValueMediator.java
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

import org.integratedmodelling.corescience.exceptions.ThinklabInexactConversionException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Triple;

/**
 * An object that adapts values from a datasource to an observation context and is capable of extracting
 * the data value and the uncertainty corresponding to a specific context state.
 * 
 * Mediators are created by conceptual models and work along one conceptual dimension only.
 * Chains of mediators are created as needed.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IValueMediator {
	
	
	/**
	 * Should return false if there is any chance that the mediation generates
	 * nonzero uncertainty. If this returns false, the version of getMediatedValue
	 * that returns a pair Ivalue, uncertainty will be used. Otherwise, the one with
	 * only the IValue return will be called and no uncertainty processing will be done.
	 * 
	 * @return
	 */
	public abstract boolean isExact();
	

	/**
	 * Perform mediation on the passed value. The current context state is passed in 
	 * case it's useful. It is assumed that any conversion is exact; if the conversion
	 * is not, it should raise an exception.
	 * @param value
	 * @param context
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IValue getMediatedValue(IValue value, IObservationContextState context) 
		throws ThinklabException;

	/**
	 * Perform mediation on the passed value. The current context state is passed in 
	 * case it's useful. It is assumed that the conversion can generate uncertainty, and
	 * the uncertainty associated with the passed value is also passed.
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public abstract Pair<IValue, IUncertainty> 
		getMediatedValue(IValue value, IUncertainty uncertainty, IObservationContextState context);
	

}
