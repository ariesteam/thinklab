/**
 * DefaultState.java
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

import java.util.Iterator;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.corescience.observation.IObservationState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.multidimensional.MultidimensionalArray;

public class DefaultState implements IObservationState {

	String id = null;
	MultidimensionalArray<IValue> state = null;
	
	public void createDataSource() throws ThinklabException {
		// TODO we should save to a table
	}

	public void initialize(
			IObservation observation,
			IObservationContext context, 
			IConceptualModel cmodel)
			throws ThinklabException {
		
		state = new MultidimensionalArray<IValue>(context.getDimensionSizes());
	}

	public void setValue(IValue value, IUncertainty uncertainty, IObservationContextState contextState) {
		state.set((int)contextState.getLinearIndex(), value);
	}

	public void clear() {
		state = null;
	}

	public double[] getDataAsDouble() throws ThinklabException {
		
		double[] ret = null;
		
		if (state != null) {
			
			if (state.size() > 0) {
				IValue val = state.iterator().next();
				if (!val.isNumber()) {
					throw new ThinklabValidationException("cannot convert state to double");
				}
			}
			
			ret = new double[state.size()];
			
			// FIXME the order only makes reliable sense if we use the same cursor type as the state
			int i = 0;
			for (Iterator<IValue> it = state.iterator(); it.hasNext(); )
				ret[i++] = it.next().asNumber().asDouble();
			
		}
		
		return ret;
	}

}
