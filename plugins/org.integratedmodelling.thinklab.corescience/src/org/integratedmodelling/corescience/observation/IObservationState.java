/**
 * IObservationState.java
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
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Represents the state of an indirect observation. Contextualization generates
 * an observation state from the connected datasource, reflecting the overall
 * context and according to the conceptual model.
 * 
 * Observation states are normally not part of the OWL model, but are generated
 * by the contextualization workflow for each indirect observation. They support
 * generation of a correspondent default datasource containing the results of
 * contextualization.
 * 
 * @author Ferdinando Villa
 * 
 */
public interface IObservationState {

	/**
	 * Should create any necessary storage for values of the concept incarnated by the 
	 * conceptual models in the passed context.
	 * 
	 * @param observation
	 * @param context
	 * @param cmodel
	 * @throws ThinklabException
	 */
	abstract void initialize(IObservation observation, IObservationContext context, IConceptualModel cmodel)
		throws ThinklabException;
	
	/**
	 * Set the passed IValue where it belongs according to the passed indexes. OR do we want to agree upon and 
	 * pass a "locator" object that we could create in the workflow?
	 * @param value
	 * @param uncertainty TODO
	 * @param contextState
	 */
	abstract void setValue(IValue value, IUncertainty uncertainty, IObservationContextState contextState);
	
	/**
	 * Should create datasources as appropriate for the observation passed in 
	 * initialization. Will be called optionally at the end if the saveState() method is 
	 * called on the workflow.
	 * 
	 * @throws ThinklabException
	 */
	abstract void createDataSource() throws ThinklabException;
	
	/**
	 * If feasible, get the data as a newly allocated array of doubles.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public double[] getDataAsDouble() throws ThinklabException;
	
	/**
	 * We won't be using this object anymore, so please clear whatever memory it's using to hold the
	 * state.
	 * 
	 */
	public void clear();
}
