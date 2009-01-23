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
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.INamedObject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

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
	
}
