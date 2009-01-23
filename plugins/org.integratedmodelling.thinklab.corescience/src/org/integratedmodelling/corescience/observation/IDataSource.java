/**
 * IDataSource.java
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
package org.integratedmodelling.corescience.observation;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;

/**
 * Base class for the implementation of a DataSource. A datasource has values, which can be distributed 
 * along dimensions each with its granularity. A datasource must be capable of returning any
 * of its values as an IValue representing a passed concept, or throw an exception. 
 * 
 * All the work of matching a datasource with a context is done by a IContextAdaptor, not by the
 * datasource. Context adaptors will know the datasource and context classes they can work.
 *  
 * @author Ferdinando Villa
 *
 */
public interface IDataSource {
	
	/**
	 * Return values for getValueType
	 * @author Ferdinando Villa
	 *
	 */
	public enum ValueType {
		IVALUE,
		LITERAL
	}
	
	/**
	 * Called once before contextualization and after the overall context has been
	 * computed. It's meant to negotiate communication parameters between the datasource
	 * and the observation state. It should analyze the contexts of the observation that
	 * the DS belongs to, and check the overall context as necessary, which may differ. Any
	 * discrepancy that cannot be negotiated (or the inability to understand the discrepancies)
	 * should cause an exception to be thrown.
	 * 
	 * The expected return value is true if the datasource can return a value using only
	 * the ordinal indexes of the extent granules in the observation context state, and false if
	 * the actual extent values (returned by IExtent.getValue()) are needed. This saves time
	 * during contextualization when getValue() is called; if true is returned, the IObservationContextState
	 * passed will only contain values for the dependencies, and not for the extents.
	 * 
	 * @param cm the conceptual model of the connected observation
	 * @param observationContext the finished context of the connected observation
	 * @param overallContext the overall context, including at least the same extents of observationContext,
	 * 		but potentially other extents compounded from other observations, and with possibly
	 * 		a finer grain and a narrower extent than what represented in the datasource.
	 * @return true if ordinal indexes can be used to return a value for a specific extent
	 * 		state; false if actual IValues are needed for each current extent.
	 * @throws ThinklabException 
	 */
	public abstract boolean handshake(
			IConceptualModel cm, 
			IObservationContext observationContext,
			IObservationContext overallContext)
		 throws ThinklabException;
	
	
	/**
	 * All datasources must report to the conceptual model whether they are prepared to return
	 * values directly, or they will return literal strings for the conceptual model to
	 * interpret. If this method returns ValueType.IVALUE, the getValue function returning a IValue will
	 * be used. The IValue is supposed to be what the conceptual model passed to handshake() knows as 
	 * value type. The IValue will be passed to the conceptual model for further (optional) validation.
	 * 
	 * If the method returns ValueType.LITERAL, the conceptual model will be in charge of
	 * interpreting the string returned by the getValueLiteral method that returns a string.
	 * 
	 * @return
	 */
	public abstract ValueType getValueType();

	
	/**
	 * This should return the IValue created from the i-th data object by validating it as the
	 * passed concept. The index array contains the linear index of each dimension in the context exposed to
	 * the DS during handshaking. The context contains the corresponding values, and will not 
	 * contain values for the extents unless handshake() has returned false AND the workflow
	 * has agreed. The response from the workflow is passed in the useExtentIndex parameter.
	 * 
	 * Note that if the datasource needs to have memory of previous states, this is the place where you need to
	 * implement it. There is no memory in Thinklab's state interface.
	 *
	 * 
	 * You only need to implement this if getValueType() was defined to return ValueType.IVALUE.
	 * 
	 * @param context the context state, containing the current state of all dependencies.
	 * @param concept the type of value to return
	 * @param useExtentIndex if true, the method must use the index methods in the context
	 * state and not the value methods. 
	 * @return a pair containing the state value from the datasource that corresponds to the passed context
	 * 			state and the associated uncertainty (or null if there is none).
	 * @throws ThinklabValidationException if anything goes wrong. FIXME should be a datasource
	 * exception or something.
	 */
	public abstract Pair<IValue, IUncertainty> getValue(
			IObservationContextState context, 
			IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException;
		
	/**
	 * Exactly like getValue, but returns a raw string literal which is passed to the conceptual model
	 * for interpretation before being set in the state.
	 * 
	 * You only need to implement this if getValueType() was defined to return ValueType.LITERAL.
	 * 
	 * @param context
	 * @param concept
	 * @param useExtentIndex
	 * @return
	 * @throws ThinklabValidationException
	 * @see getValue
	 */
	public abstract Pair<String, IUncertainty> getValueLiteral(
			IObservationContextState context, 
			IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException;
		

	/**
	 * Datasources may have an initial value before anything is computed or extracted. This value, if
	 * not null, is used to initialize state storage before contextualization. If the initial value
	 * makes sense for the datasource, return it here. Otherwise just return null.
	 * 
	 * @return
	 */
	public abstract Pair<IValue, IUncertainty> getInitialValue();

}
