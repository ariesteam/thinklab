package org.integratedmodelling.corescience.workflow;

import org.integratedmodelling.corescience.interfaces.IContextualizer;

/*
 * Testing - not used at all for now.
 * Contextualizer class for simple workflows.
 */
public class SimpleContextualizer implements IContextualizer {

	static final byte F_ACTIVE = 0x01;
	static final byte F_MEDIATED = 0x01;
	
	static int n_obs = 0;
	
	byte[] oflags = null;

	@Override
	public void run() {

		// allocate arrays for specified ctx and states
		
	}
	
}
