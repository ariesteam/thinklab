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
package org.integratedmodelling.thinklab.modelling.context;

import java.util.HashMap;

import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.compiler.IContextualizer;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;

public class Contextualizer implements IContextualizer {

	private IObservation obs;
	private IContext context;
	private HashMap<IInstance, IState> known;

	public Contextualizer(IObservation obs, IContext context, HashMap<IInstance, IState> known) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.known = known;
		this.obs = obs;
	}
	
	
	
	/**
	 * Contextualize the whole thing and return the resulting context.
	 * 
	 * @return
	 */
	public IContext run() {
		
		/*
		 * compute overall context
		 */
		
		
		/*
		 * define and compute all transformations to populate
		 * the context with the initial states.
 		 */

		/*
		 * compile observation tree into accessor sequence
		 * and run.
		 */
		
		
		/*
		 * create final context with computed states and without
		 * listeners or other BS.
		 */
		
		return context;
		
	}




}
