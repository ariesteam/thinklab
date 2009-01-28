/**
 * IConceptualModel.java
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

import org.integratedmodelling.corescience.exceptions.ThinklabConceptualModelValidationException;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.INamedObject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;

/**
 * Each indirect observation has its state described by "data". A conceptual
 * model holds the secret that allows to make sense of the data. The instance of
 * the CM linked to the observation knows what the data mean and if/how they can
 * be converted when links are made between observations that represent the differently.

 * @author Ferdinando Villa
 * @model
 */
public interface IConceptualModel extends INamedObject {

	/**
	 * Return the concept that implements storage for one grain of this
	 * conceptual model. FIXME could also return a IValue directly. The idea for
	 * this is that we use it to create IValues to make a state vector out of.
	 * 
	 * We assume the convention that if the IConcept returned is null, the
	 * storage model is a generic concept and the values are objects or classes.
	 * Otherwise we could return the root concept from the KM, but it makes
	 * everything more complicated.
	 * 
	 * @return
	 * @model
	 */
	public abstract IConcept getStateType();
	
	/**
	 * Return the type that uncertainty is characterized with. If no uncertainty model is
	 * used, return KnowledgeManager.Nothing().
	 * 
	 * @return
	 */
	public abstract IConcept getUncertaintyType();

	/**
	 * Called every time a datasource has returned a string literal for the passed context state. The
	 * CM is in charge of converting it to an appropriate value or to raise a validation error. The 
	 * returned IValue will be used as the state for that context state.
	 * 
	 * @param value
	 * @param contextState
	 * @return
	 * @throws ThinklabValidationException
	 */
	public abstract IValue validateLiteral(String value, IObservationContextState contextState) 
		throws ThinklabValidationException;
	
	/**
	 * Check that CM is internally consistent and that it fits the passed observation appropriately. 
	 * 
	 * @throws ThinklabValidationException 
	 * @throws ThinklabConceptualModelValidationException if inconsistent
	 */
	public abstract void validate(IObservation observation) throws ThinklabValidationException;
	
//	/**
//	 * Called once to communicate the overall context and to negotiate communication with the
//	 * datasource. Called whether or not a datasource is actually connected, BEFORE the twin 
//	 * handshake() is called on the datasource itself.
//	 * 
//	 * @param cm
//	 * @param observationContext
//	 * @param overallContext
//	 * @return
//	 * @throws ThinklabException
//	 */
//	public abstract boolean handshake(
//			IObservationContext observationContext,
//			IObservationContext overallContext,
//			IDataSource ds)
//		 throws ThinklabException;
//	
//	
//
//	
//	/**
//	 * This should return the IValue created from the i-th data object by validating it as the
//	 * passed concept. The index array contains the linear index of each dimension in the context exposed to
//	 * the DS during handshaking. The context contains the corresponding values, and will not 
//	 * contain values for the extents unless handshake() has returned false AND the workflow
//	 * has agreed. The response from the workflow is passed in the useExtentIndex parameter.
//	 * 
//	 * Note that if the datasource needs to have memory of previous states, this is the place where you need to
//	 * implement it. There is no memory in Thinklab's state interface.
//	 *
//	 * 
//	 * You only need to implement this if getValueType() was defined to return ValueType.IVALUE.
//	 * 
//	 * @param context the context state, containing the current state of all dependencies.
//	 * @param concept the type of value to return
//	 * @param useExtentIndex if true, the method must use the index methods in the context
//	 * state and not the value methods. 
//	 * @return a pair containing the state value from the datasource that corresponds to the passed context
//	 * 			state and the associated uncertainty (or null if there is none).
//	 * @throws ThinklabValidationException if anything goes wrong. FIXME should be a datasource
//	 * exception or something.
//	 */
//	public abstract Pair<IValue, IUncertainty> getValue(
//			IObservationContextState context, 
//			IConcept concept,
//			boolean useExtentIndex) throws ThinklabValidationException;
//		
//	/**
//	 * Exactly like getValue, but returns a raw string literal which is passed to the conceptual model
//	 * for interpretation before being set in the state.
//	 * 
//	 * You only need to implement this if getValueType() was defined to return ValueType.LITERAL.
//	 * 
//	 * @param context
//	 * @param concept
//	 * @param useExtentIndex
//	 * @return
//	 * @throws ThinklabValidationException
//	 * @see getValue
//	 */
//	public abstract Pair<String, IUncertainty> getValueLiteral(
//			IObservationContextState context, 
//			IConcept concept,
//			boolean useExtentIndex) throws ThinklabValidationException;
//		
//
//	/**
//	 * Datasources may have an initial value before anything is computed or extracted. This value, if
//	 * not null, is used to initialize state storage before contextualization. If the initial value
//	 * makes sense for the datasource, return it here. Otherwise just return null.
//	 * 
//	 * @return
//	 */
//	public abstract Pair<IValue, IUncertainty> getInitialValue();
	
}
