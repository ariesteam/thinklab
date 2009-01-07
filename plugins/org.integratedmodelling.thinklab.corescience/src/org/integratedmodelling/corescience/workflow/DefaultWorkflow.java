/**
 * DefaultWorkflow.java
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
package org.integratedmodelling.corescience.workflow;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationState;
import org.integratedmodelling.corescience.observation.Observation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

/**
 * The default workflow class will store the observations and context states and just
 * create and run mediators on the fly. The result of running the default workflow
 * is the same observation that has been contextualized, with the computed states set into
 * each observations' implementation.
 * 
 * @author Ferdinando Villa
 *
 */
public class DefaultWorkflow extends AsynchronousContextualizationWorkflow {


	@Override
	public IValue generateResult(IObservation observation, IObservationContext context) throws ThinklabException {

		/*
		 * set the topmost context only
		 */
		((Observation)observation).setCurrentObservationContext(context);
		
		/*
		 * set the result 
		 */
		for (ActivationRecord ar : getActivationRecords()) {
			((Observation)ar.observation).setObservationState(ar.state);
		}
		
		/*
		 * return the same observation, after making sure that we have connected the calculated states to 
		 * each obs.
		 */
		return new ObjectReferenceValue(observation.getObservationInstance());
	
	}

	public IObservationState createObservationState(IObservation observation, IConcept stateType, long nStates) throws ThinklabException {
		return new DefaultState();
	}

}
