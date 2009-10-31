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
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Each indirect observation has its state described by "data". A conceptual
 * model holds the secret that allows to make sense of the data. The instance of
 * the CM linked to the observation knows what the data mean and if/how they can
 * be converted when links are made between observations that represent the differently.

 * @author Ferdinando Villa
 * @model
 */
public interface IConceptualModel {

	/**
	 * Return the concept that implements storage for one grain of this
	 * conceptual model. 
	 * 
	 * If the observation is not supposed to have any state, the return value should
	 * be KnowledgeManager.Nothing(). 
	 * 
	 * @return a concept. Do not return null.
	 * @model
	 */
	public abstract IConcept getStateType();
	
	/**
	 * Check that CM is internally consistent and that it fits the passed observation appropriately. 
	 * NOTE: do NOT store the datasource from the observation. It may end up becoming a different
	 * one, which is communicated to handshake() later, so that's the place for that.
	 * 
	 * @throws ThinklabValidationException 
	 * @throws ThinklabConceptualModelValidationException if inconsistent
	 */
	public abstract void validate(IObservation observation) throws ThinklabValidationException;

	/**
	 * Return the state accessor that will be asked to produce states at contextualization. It will
	 * also be notified of all dependencies and the types the compiler has chosen for them.
	 * 
	 * @param context
	 * @return
	 */
	public abstract IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context);
	
	
	/**
	 * Called once to communicate the overall context and to negotiate communication with the
	 * datasource. Called only if a datasource is actually connected, AFTER the twin 
	 * handshake() is called on the datasource itself and had a chance to return a different
	 * datasource than the one originally intended (e.g. after interpolation).
	 * 
	 * @param cm
	 * @param observationContext
	 * @param overallContext
	 * @return
	 * @throws ThinklabException
	 */
	public abstract void handshake(
			IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext)
		 throws ThinklabException;

	/**
	 * Create a customized datasource to hold the results of contextualization. Only called if
	 * the state type is not a number. Should raise an exception in case of problem, never return
	 * null or bull.
	 * 
	 * @param size
	 * @return
	 */
	public abstract IContextualizedState createContextualizedStorage(int size) 
		throws ThinklabException;



	
}
