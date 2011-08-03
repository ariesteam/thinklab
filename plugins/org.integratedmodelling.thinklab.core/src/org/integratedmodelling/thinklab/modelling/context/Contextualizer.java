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
