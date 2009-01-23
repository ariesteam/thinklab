/**
 * DebugState.java
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
package org.integratedmodelling.corescience.workflow.debug;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.corescience.observation.IObservationState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * A state holder that simply logs the states submitted. Used for debugging only, created by 
 * DebugWorkflow.
 * 
 * @author Ferdinando Villa
 *
 */
public class DebugState implements IObservationState {

	private IObservation observation;
	private IObservationContext context;
	private IConceptualModel cmodel;
	
	public void createDataSource() throws ThinklabException {
	}

	public void initialize(IObservation observation,
			IObservationContext context, IConceptualModel cmodel)
			throws ThinklabException {
		
		this.observation = observation;
		this.context = context;
		this.cmodel = cmodel;
	}

	public void setValue(IValue value, IUncertainty uncertainty, IObservationContextState contextState) {
	
		System.out.println(cmodel.getObjectName() + " = " + value + " @ " + contextState );
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public double[] getDataAsDouble() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
