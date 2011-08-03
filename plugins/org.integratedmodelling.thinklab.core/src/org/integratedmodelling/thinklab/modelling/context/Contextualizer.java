package org.integratedmodelling.thinklab.modelling.context;

import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;

public class Contextualizer {

	private IObservation obs;
	private IContext context;
	private IKBox kbox;

	public Contextualizer(IObservation obs, IContext context, IKBox kbox) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.kbox = kbox;
		this.obs = obs;
	}
	

	
	/**
	 * Contextualize the whole thing and return the resulting context.
	 * 
	 * @return
	 */
	public IContext run() {
		

		

		
		/*
		 * 
		 */
		
		return context;
		
	}

}
