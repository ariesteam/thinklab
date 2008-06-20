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
package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.corescience.exceptions.ThinklabConceptualModelValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.INamedObject;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.jscience.mathematics.number.Rational;

/**
 * Each indirect observation has its state described by "data". A conceptual
 * model holds the secret that allows to make sense of the data. The instance of
 * the CM linked to the observation knows what the data mean and if/how they can
 * be converted when links are made between observations that represent the differently.
 * 
 * @author Ferdinando Villa
 * @model
 */
public interface IConceptualModel extends INamedObject {
	
	
	/**
	 * Return an appropriate mediator to convert a state in a given
	 * overall context. The context adaptor should be initialized and stored to
	 * handle all states of the context.
	 * 
	 * @param overallContext
	 * @param datasource
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public abstract IValueMediator getMediator(IConceptualModel conceptualModel, IObservationContext ctx) throws ThinklabException;
 
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
	 * Return an aggregator suitable for the given contexts and the types we represent, or null
	 * if no aggregator is required. Typically when an aggregator is requested, at least one
	 * extent has produced a nontrivial mediator, meaning that there are scaling discrepancies
	 * involving the associated observation.
	 * 
	 * @param ownContext
	 * @param ownContext
	 * @return
	 */
	public abstract IValueAggregator getAggregator(IObservationContext ownContext, 
			IObservationContext overallContext);
	
	/**
	 * Partition the passed value into what makes sense for a new extent which stands in the
	 * passed ratio with the original one. If partitioning makes no sense for this type and
	 * concept, just return the original value.
	 * 
	 * @param originalValue
	 * @param ratio
	 * @return
	 */
	public abstract IValue partition(IValue originalValue, Rational ratio);
	
	/**
	 * Check that CM is internally consistent and that it fits the passed observation appropriately. 
	 * 
	 * @throws ThinklabValidationException 
	 * @throws ThinklabConceptualModelValidationException if inconsistent
	 */
	public abstract void validate(IObservation observation) throws ThinklabValidationException;
	
	/**
	 * Called every time a datasource has returned a value for the passed context state. Gives the
	 * CM a chance to further validate it, e.g. checking boundaries, type etc. The returned IValue
	 * will be used as the state for that context state.
	 * 
	 * @param value
	 * @param contextState
	 * @return
	 * @throws ThinklabValidationException
	 */
	public abstract IValue validateValue(IValue value, IObservationContextState contextState) 
		throws ThinklabValidationException;

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
	 * Can be called by the datasource when it does not know what to do with a byte value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(byte b) throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a int value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(int b) throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a long value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(long b) throws ThinklabValidationException;

	
	/**
	 * Can be called by the datasource when it does not know what to do with a float value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(float b) throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a double value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(double b) throws ThinklabValidationException;


}
