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
package org.integratedmodelling.modelling.implementations.observations;

import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.modelling.agents.SPANK;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Support for running the Clojure SPANK models and making lazy observation proxies to its
 * results.  
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept="modeltypes:SPANKTransformer")
public class SPANKTransformer 
	extends Observation 
	implements TransformingObservation {
	
	// set by model through reflection
	public HashMap<String, Object> parameters;

	
	@Override
	public IConcept getTransformedObservationClass() {
		return CoreScience.Observation();
	}

	@Override
	public IContext transform(IObservationContext sourceCtx, ISession session,
			IContext context) throws ThinklabException {
		
		/*
		 * create the appropriate SPANK model and run it
		 */
		SPANK spank = 
			SPANK.getSpankModel(
				sourceCtx.getObservation().getObservableClass(),
				context,
				parameters == null ? new HashMap<String,Object>() : parameters);
		
		ObservationContext ret = new ObservationContext(context.getExtents());
		ret.setObservation(this);
		for (IState st : spank.run()) {
			ret.addState(st);
		}
		
		return ret;
	}
}