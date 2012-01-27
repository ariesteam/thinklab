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
package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.HashMap;

import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;

/**
 * The reference implementation of the model object created by defmodel. It holds one
 * or more concrete models (e.g. measurements, rankings etc) that can represent the
 * observable, and is capable of selecting the applicable one(s) appropriately to 
 * observe the observable in a given context, generating the Observation structure
 * that can be resolved to States through contextualization.
 *  
 * Compared to any other model, it supports:
 * 
 * <ol>
 * <li>a context model that is computed before the others and drives the choice of
 *     contingent models;</li>
 * <li>multiple models (contingencies) to be selected on the basis of the context
 * 	   model if present, or on previous models not being defined in each specific
 * 	   context state;</li>
 * </ol>
 * 	
 * @author Ferd
 *
 */
public class Model extends DefaultAbstractModel {

	IModel _contextModel = null;
		
	public Model(INamespace namespace) {
		super(namespace);
	}
	

	@Override
	public IObservation createObservation(HashMap<IInstance, IState> known) {
		// TODO Auto-generated method stub
		return null;
	}


}
