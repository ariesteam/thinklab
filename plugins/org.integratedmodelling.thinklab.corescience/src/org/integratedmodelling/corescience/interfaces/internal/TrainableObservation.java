/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Trainable observations can be fed with expected outputs as well as input
 * dependencies, and produce a new observation with updated internal 
 * configuration or dependent states that gives the best approximation of
 * the outputs.
 * 
 * 
 * 
 * @author Ferdinando Villa
 *
 */
public interface TrainableObservation extends IObservation {
	
	/**
	 * Train the observation and return a new observation with adjusted
	 * internal configuration or dependencies.
	 * 
	 * @return
	 */
	public IInstance train(IInstance sourceObs, ISession session, IObservationContext context) throws ThinklabException;
	
}
